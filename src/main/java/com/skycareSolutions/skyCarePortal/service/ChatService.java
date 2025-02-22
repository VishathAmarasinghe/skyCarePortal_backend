package com.skycareSolutions.skyCarePortal.service;

import com.mysql.cj.log.Log;
import com.skycareSolutions.skyCarePortal.dto.*;
import com.skycareSolutions.skyCarePortal.model.*;
import com.skycareSolutions.skyCarePortal.repository.ChatMemberRepository;
import com.skycareSolutions.skyCarePortal.repository.ChatRepository;
import com.skycareSolutions.skyCarePortal.repository.ClientRepository;
import com.skycareSolutions.skyCarePortal.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

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

    public Message saveMessage(ChatMessageDTO messageDTO){
        Chat chat = chatRepository.findById(messageDTO.getChatID())
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSenderID(messageDTO.getSenderID());
        message.setContent(messageDTO.getContent());
        return messageRepository.save(message);
    }

    public List<Message> getMessages(Long chatId) {
        return messageRepository.findByChatId(chatId);
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


    public List<ChatDTO> getListOfChatsByUser(String userID){
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
