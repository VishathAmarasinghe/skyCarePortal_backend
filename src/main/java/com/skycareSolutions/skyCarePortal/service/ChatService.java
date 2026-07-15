package com.skycareSolutions.skyCarePortal.service;

import com.mysql.cj.log.Log;
import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.*;
import com.skycareSolutions.skyCarePortal.utils.TemporaryHiddenClients;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ChatService {

    private static final String DIRECTORY = Paths.get(System.getProperty("user.home"), "skyCareUploads", "chatDocuments").toString();

    @Lazy
    @Autowired
    private ChatRepository chatRepository;

    @Lazy
    @Autowired
    private ChatMemberRepository chatMemberRepository;

    @Lazy
    @Autowired
    private ClientService clientService;

    @Lazy
    @Autowired
    private EmployeeService employeeService;

    @Lazy
    @Autowired
    private MessageRepository messageRepository;

    @Lazy
    @Autowired
    private MessageSeenRepository messageSeenRepository;

    @Lazy
    @Autowired
    private ChatDocumentRepository chatDocumentRepository;

    public String createPrivateChat(String user1, String user2){

        if(user1.substring(0,2).equalsIgnoreCase("CU") && user2.substring(0,2).equalsIgnoreCase("EM")){
            Client clinet = clientService.getClientByIdWithEntitiy(user1);
            Employee employee = employeeService.findOneEmployeeWithEntity(user2);
            List<Long> privateChatCheck = chatMemberRepository.findExistingPrivateChatWithClientAndEmployee(clinet,employee);
            if (privateChatCheck.size()>0){
                throw new RuntimeException("Private Chat already exist");
            }
        } else if(user1.substring(0,2).equalsIgnoreCase("EM") && user2.substring(0,2).equalsIgnoreCase("EM")){
            Employee employee = employeeService.findOneEmployeeWithEntity(user2);
            Employee employee1 = employeeService.findOneEmployeeWithEntity(user1);
            List<Long> privateChatCheck = chatMemberRepository.findExistingPrivateChatWithTwoEmployees(employee1,employee);
            if (privateChatCheck.size()>0){
                throw new RuntimeException("Private Chat already exist");
            }
        }
        Chat chat = new Chat();
        chat.setChatType(ChatType.PRIVATE);

        Chat savedChat = chatRepository.save(chat);

        if(user1.substring(0,2).equalsIgnoreCase("CU")){
            Client clinet = clientService.getClientByIdWithEntitiy(user1);
            chatMemberRepository.save(new ChatMember(savedChat,null,clinet));

        } else if(user1.substring(0,2).equalsIgnoreCase("EM")){
            Employee employee = employeeService.findOneEmployeeWithEntity(user1);
            chatMemberRepository.save(new ChatMember(savedChat, employee,null));
        }
        if (user2.substring(0,2).equalsIgnoreCase("EM")){
            Employee employee = employeeService.findOneEmployeeWithEntity(user2);
            chatMemberRepository.save(new ChatMember(savedChat, employee,null));
        } else  if (user2.substring(0,2).equalsIgnoreCase("CU")){
            Client clinet = clientService.getClientByIdWithEntitiy(user2);
            chatMemberRepository.save(new ChatMember(savedChat,null,clinet));
        }

        return "private chat created successfully";
    }

    public Chat createPublicChat(GroupChatRequestDTO groupChatRequest){
        Chat chat = new Chat();
        chat.setChatType(ChatType.GROUP);
        chat.setName(groupChatRequest.getGroupName());

        Chat savedChat = chatRepository.save(chat);

        for (String user:groupChatRequest.getUserIDs()){
            if(user.substring(0,2).equalsIgnoreCase("CU")){
                Client clinet = clientService.getClientByIdWithEntitiy(user);
                chatMemberRepository.save(new ChatMember(savedChat,null,clinet));
            }else if (user.substring(0,2).equalsIgnoreCase("EM")){
                Employee employee = employeeService.findOneEmployeeWithEntity(user);
                chatMemberRepository.save(new ChatMember(savedChat, employee,null));
            }
        }
        return savedChat;
    }


    public HashMap<Long, Integer> getUnseenMessagesForUser(String userId) {
        List<MessageSeen> messageSeenList = messageSeenRepository.findUnseenMessagesByUserId(userId);
        HashMap<Long, Integer> messageMap = new HashMap<>();

        for (MessageSeen messageSeen : messageSeenList) {
            Long chatId = messageSeen.getChat().getId();
            // If the chatId is not in the map, add it with count 1, else increment count
            messageMap.put(chatId, messageMap.getOrDefault(chatId, 0) + 1);
        }

        return messageMap;
    }

    @Transactional
    public String updateMessageSeen(String userID,long chatID){
        List<MessageSeen> messageSeenList = messageSeenRepository.findUnseenMessagesByUserId(userID);
        if (messageSeenList.size()>0){
            messageSeenRepository.markMessagesAsSeen(chatID,userID);
        }
        return "updated message seen";
    }

    @Transactional
    public Chat updateChat(GroupChatRequestDTO groupChatRequest,long chatId){

        Optional<Chat> chatInstance = chatRepository.findById(chatId);
        if (chatInstance.isPresent()){
            Chat chat = chatInstance.get();
            if (groupChatRequest.getUserIDs().size()>2){
                chat.setChatType(ChatType.GROUP);
                chat.setName(groupChatRequest.getGroupName());
            }
            List<ChatMemberDTO> exisitingMembers = getAllChatMembers(chatId);
            for (String user:groupChatRequest.getUserIDs()){
                if (!exisitingMembers.contains(user)){
                    if(user.substring(0,2).equalsIgnoreCase("CU")){
                        Client clinet = clientService.getClientByIdWithEntitiy(user);
                        chatMemberRepository.save(new ChatMember(chat,null,clinet));
                    }else if (user.substring(0,2).equalsIgnoreCase("EM")){
                        Employee employee = employeeService.findOneEmployeeWithEntity(user);
                        chatMemberRepository.save(new ChatMember(chat, employee,null));
                    }
                }
            }

            for (ChatMemberDTO member:exisitingMembers){
                if (!groupChatRequest.getUserIDs().contains(member.getUserID())){
                    if(member.getUserID().substring(0,2).equalsIgnoreCase("CU")){
                        Client client = clientService.getClientByIdWithEntitiy(member.getUserID());
                        chatMemberRepository.deleteByChatIdAndClientId(chat.getId(),client);
                    }else if (member.getUserID().substring(0,2).equalsIgnoreCase("EM")){
                        Employee employee = employeeService.findOneEmployeeWithEntity(member.getUserID());
                        chatMemberRepository.deleteByChatIdAndEmployeeId(chat.getId(),employee);
                    }
                }
            }
            Chat savedChat = chatRepository.save(chat);
            return savedChat;
        }else{
            throw new RuntimeException();
        }

    }

    public MessageBroadcasterDTO saveMessage(ChatMessageDTO messageDTO){
        Chat chat = chatRepository.findById(messageDTO.getChatID())
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSenderID(messageDTO.getSenderID());
        message.setContent(messageDTO.getContent());
        message=messageRepository.save(message);

        String senderFullName="";
        String senderProfilePic="";

        MessageBroadcasterDTO messageBroadcasterDTO = new MessageBroadcasterDTO();
        List<String> broadcastList = new ArrayList<>();


        List<Client> clientList = chatMemberRepository.findClientsByChatId(chat.getId());
        List<Employee> employeeList = chatMemberRepository.findEmployeesByChatId(chat.getId());

        for (Client client:clientList){
            if (!client.getClientID().equalsIgnoreCase(messageDTO.getSenderID())){
                MessageSeen messageSeen = new MessageSeen();
                messageSeen.setChat(chat);
                messageSeen.setMessage(message);
                messageSeen.setReadStatus(false);
                messageSeen.setUserId(client.getClientID());
                messageSeenRepository.save(messageSeen);
            }else{
                senderFullName=client.getFirstName()+" "+client.getLastName();
                senderProfilePic=client.getProfilePhoto();
            }
            broadcastList.add(client.getClientID());
        }

        for (Employee employee:employeeList){
            if (!employee.getEmployeeID().equalsIgnoreCase(messageDTO.getSenderID())){
                MessageSeen messageSeen = new MessageSeen();
                messageSeen.setChat(chat);
                messageSeen.setMessage(message);
                messageSeen.setReadStatus(false);
                messageSeen.setUserId(employee.getEmployeeID());

                messageSeenRepository.save(messageSeen);
            }else{
                senderFullName=employee.getFirstName()+" "+employee.getLastName();
                senderProfilePic=employee.getProfile_photo();
            }
            broadcastList.add(employee.getEmployeeID());
        }

        MessageDTO chatResult = new MessageDTO();
        chatResult.setChatID(chat.getId());
        chatResult.setContent(message.getContent());
        chatResult.setSenderID(message.getSenderID());
        chatResult.setTimeStamp(message.getTimestamp());
        chatResult.setMessageID(message.getId());
        chatResult.setSenderName(senderFullName);
        chatResult.setSenderProfilePic(senderProfilePic);


        messageBroadcasterDTO.setMessageData(chatResult);
        messageBroadcasterDTO.setBroadcastList(broadcastList);
        return messageBroadcasterDTO;
    }



    public List<ChatUserDTO> getAcceptedChatUsersForSelectedUser(String userID){
        List<ChatUserDTO> chatUserList = new ArrayList<>();
        if (userID.substring(0,2).equalsIgnoreCase("CU")){
            List<EmployeeDTO> employees = employeeService.getEmployeesByRole("Admin");
            for (EmployeeDTO emp:employees){
                ChatUserDTO singleUser = new ChatUserDTO();
                singleUser.setUserID(emp.getEmployeeID());
                singleUser.setFirstName(emp.getFirstName());
                singleUser.setLastName(emp.getLastName());
                singleUser.setProfilePhoto(emp.getProfile_photo());
                singleUser.setUserRole("Admin");
                chatUserList.add(singleUser);
            }
        }else{
            Employee selectedEmployee = employeeService.findOneEmployeeWithEntity(userID);
            if (selectedEmployee.getAccessRole().equalsIgnoreCase("Admin")){
                List<MetaEmployeeDetailsDTO> employees = employeeService.getAllEmployees();
                for (MetaEmployeeDetailsDTO emp:employees){
                    ChatUserDTO singleUser = new ChatUserDTO();
                    singleUser.setUserID(emp.getEmployeeID());
                    singleUser.setFirstName(emp.getFirstName());
                    singleUser.setLastName(emp.getLastName());
                    singleUser.setProfilePhoto(emp.getProfile_photo());
                    singleUser.setUserRole(emp.getAccessRole());
                    chatUserList.add(singleUser);
                }
                List<ClientDTO> clients = clientService.getAllClients();
                for (ClientDTO client:clients){
                    ChatUserDTO singleUser = new ChatUserDTO();
                    singleUser.setUserID(client.getClientID());
                    singleUser.setFirstName(client.getFirstName());
                    singleUser.setLastName(client.getLastName());
                    singleUser.setProfilePhoto(client.getProfilePhoto());
                    singleUser.setUserRole("Client");
                    chatUserList.add(singleUser);
                }

            }else{
                List<EmployeeDTO> employees = employeeService.getEmployeesByRole("Admin");
                for (EmployeeDTO emp:employees){
                    ChatUserDTO singleUser = new ChatUserDTO();
                    singleUser.setUserID(emp.getEmployeeID());
                    singleUser.setFirstName(emp.getFirstName());
                    singleUser.setLastName(emp.getLastName());
                    singleUser.setProfilePhoto(emp.getProfile_photo());
                    chatUserList.add(singleUser);
                }
            }
        }
        return chatUserList;
    }

    public String deleteMessage(long messageId){
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent()){
            List<MessageSeen> messageSeenList = messageSeenRepository.findByMessage(message.get());
            for (MessageSeen seenOne:messageSeenList){
                messageSeenRepository.deleteById(seenOne.getId());
            }
        }
        messageRepository.deleteById(messageId);
        return "Deleted successfully";
    }



    public List<String> addChatDocument(List<MultipartFile> files) {
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile doc : files) {
            long docID=1;
            Optional<ChatDocuments> lastDocument = chatDocumentRepository.findFirstByOrderByDocumentIdDesc();
            if (lastDocument.isPresent()){
                docID=lastDocument.get().getDocumentId();
            }
            ChatDocuments chatDocument = new ChatDocuments();
            if (files!=null && files.size()>0){
                    try {

                        String originalFileName = doc.getOriginalFilename();
                        String fileExtension = originalFileName;
                        if (originalFileName != null && originalFileName.contains(".")) {
                            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                        }
                        File directory = new File(DIRECTORY);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        File destinationFile = new File(DIRECTORY, originalFileName+(docID+1)+"_" + fileExtension);
                        doc.transferTo(destinationFile);
                        chatDocument.setDocumentLocation(destinationFile.getAbsolutePath());
                        filePaths.add(destinationFile.getAbsolutePath());
                        chatDocumentRepository.save(chatDocument);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
            }
        }
        return filePaths;
    }

    public String deleteChatMember(long chatID,String userID){

            try{
                if (userID.substring(0,2).equalsIgnoreCase("CU")){
                    Client client = clientService.getClientByIdWithEntitiy(userID);
                    chatMemberRepository.deleteByChatIdAndClientId(chatID,client);
                }else if (userID.substring(0,2).equalsIgnoreCase("EM")){
                    Employee employee = employeeService.findOneEmployeeWithEntity(userID);
                    chatMemberRepository.deleteByChatIdAndEmployeeId(chatID,employee);
                }
                return "User deleted successfully";
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

    public List<ChatMemberDTO> getAllChatMembers(long chatID){
        List<Employee> memberList = chatMemberRepository.findEmployeesByChatId(chatID);
        List<Client> memberClientList = chatMemberRepository.findClientsByChatId(chatID);
        List<ChatMemberDTO> resultList = new ArrayList<>();
        for (Employee emp:memberList){
            ChatMemberDTO member = new ChatMemberDTO();
            member.setUserID(emp.getEmployeeID());
            member.setUserName(emp.getFirstName()+" "+emp.getLastName());
            member.setUserProfilePhoto(emp.getProfile_photo());
            member.setUserRole(emp.getAccessRole());
            resultList.add(member);
        }
        for (Client cl:memberClientList){
            if (TemporaryHiddenClients.isHidden(cl.getClientID())) {
                continue;
            }
            ChatMemberDTO member = new ChatMemberDTO();
            member.setUserID(cl.getClientID());
            member.setUserName(cl.getFirstName()+" "+cl.getLastName());
            member.setUserProfilePhoto(cl.getProfilePhoto());
            member.setUserRole("Client");
            resultList.add(member);
        }
        return resultList;
    }

    public List<MessageDTO> getAllMessagesRelatedToChat(Long chatId){

        List<Message> messageList = messageRepository.findByChatId(chatId);
        List<MessageDTO> resultList = new ArrayList<>();
        for (Message message:messageList){
            if (TemporaryHiddenClients.isHidden(message.getSenderID())) {
                continue;
            }
            MessageDTO singleMessage = new MessageDTO();
            singleMessage.setMessageID(message.getId());
            singleMessage.setChatID(message.getChat().getId());
            singleMessage.setContent(message.getContent());
            singleMessage.setSenderID(message.getSenderID());
            singleMessage.setTimeStamp(message.getTimestamp());

            if (message.getSenderID().substring(0,2).equalsIgnoreCase("CU")){
                Client client = clientService.getClientByIdWithEntitiy(message.getSenderID());
                singleMessage.setSenderProfilePic(client.getProfilePhoto());
                singleMessage.setSenderName(client.getFirstName()+" "+client.getLastName());
            }else if (message.getSenderID().substring(0,2).equalsIgnoreCase("EM")){
                Employee employee = employeeService.findOneEmployeeWithEntity(message.getSenderID());
                singleMessage.setSenderName(employee.getFirstName()+" "+employee.getLastName());
                singleMessage.setSenderProfilePic(employee.getProfile_photo());
            }
            resultList.add(singleMessage);
        }
        return resultList;
    }


    public List<ChatDTO> getListOfChatsByUser(String userID){
        if (TemporaryHiddenClients.isHidden(userID)) {
            return List.of();
        }
        List<Long> chatList = new ArrayList<>();
        List<ChatDTO> resutlList = new ArrayList<>();
        String userType = "Client";
        if (userID.substring(0,2).equalsIgnoreCase("CU")){
            Client clinet = clientService.getClientByIdWithEntitiy(userID);
            chatList = chatMemberRepository.findChatsByClientId(clinet);
        }else if (userID.substring(0,2).equalsIgnoreCase("EM")){
            userType="employee";
            Employee employee = employeeService.findOneEmployeeWithEntity(userID);
            chatList = chatMemberRepository.findChatsByEmployee(employee);
        }
        for (Long chatID:chatList){
            Optional<Chat> chat = chatRepository.findById(chatID);
            if (chat.isPresent()){
                Chat chatData = chat.get();
                ChatDTO singleResult = new ChatDTO();
                singleResult.setChatID(chatData.getId());
                if (chatData.getChatType().equals(ChatType.GROUP)){
                    singleResult.setChatName(chatData.getName());
                    singleResult.setGroup(true);
                }else{
                    singleResult.setGroup(false);
                    if (userType.equalsIgnoreCase("Client")){
                        List<Employee> employee = chatMemberRepository.findEmployeesByChatId(chatData.getId());
                        for (Employee emp:employee){
                            singleResult.setChatName(emp.getFirstName()+emp.getLastName());
                            singleResult.setChatIcon(emp.getProfile_photo());
                        }
                    }else{
                        List<Employee> employeeSet = chatMemberRepository.findEmployeesByChatId(chatData.getId());
                        List<Client> clientSet = chatMemberRepository.findClientsByChatId(chatData.getId());
                        for (Employee emp:employeeSet){
                            if (!emp.getEmployeeID().equalsIgnoreCase(userID)){
                                singleResult.setChatIcon(emp.getProfile_photo());
                                singleResult.setChatName(emp.getFirstName()+emp.getLastName());
                            }
                        }
                        for(Client clt:clientSet){
                            if (TemporaryHiddenClients.isHidden(clt.getClientID())) {
                                continue;
                            }
                            if (!clt.getClientID().equalsIgnoreCase(userID)){
                                singleResult.setChatIcon(clt.getProfilePhoto());
                                singleResult.setChatName(clt.getFirstName()+clt.getLastName());
                            }
                        }
                    }
                }
                resutlList.add(singleResult);
            }

        }
        return resutlList;
    }
}
