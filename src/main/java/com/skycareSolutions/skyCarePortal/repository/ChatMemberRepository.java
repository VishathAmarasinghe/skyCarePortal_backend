package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.ChatMember;
import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.model.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface

ChatMemberRepository extends CrudRepository<ChatMember,Long> {

    @Query("SELECT cm.chat.id FROM ChatMember cm " +
            "WHERE cm.chat.id IN ( " +
            "    SELECT cm1.chat.id FROM ChatMember cm1 " +
            "    WHERE cm1.client = :client " +
            ") AND cm.chat.id IN ( " +
            "    SELECT cm2.chat.id FROM ChatMember cm2 " +
            "    WHERE cm2.employee = :employee " +
            ") " +
            "GROUP BY cm.chat.id HAVING COUNT(cm.chat.id) = 2")
    List<Long> findExistingPrivateChatWithClientAndEmployee(
            @Param("client") Client client,
            @Param("employee") Employee employee
    );

    @Query("SELECT cm.chat.id FROM ChatMember cm " +
            "WHERE cm.chat.id IN ( " +
            "    SELECT cm1.chat.id FROM ChatMember cm1 " +
            "    WHERE cm1.employee = :employee1 " +
            ") AND cm.chat.id IN ( " +
            "    SELECT cm2.chat.id FROM ChatMember cm2 " +
            "    WHERE cm2.employee = :employee2 " +
            ") " +
            "GROUP BY cm.chat.id HAVING COUNT(cm.chat.id) = 2")
    List<Long> findExistingPrivateChatWithTwoEmployees(@Param("employee1") Employee employee1, @Param("employee2") Employee employee2);

    @Query("SELECT DISTINCT cm.chat.id FROM ChatMember cm WHERE cm.client = :client")
    List<Long> findChatsByClientId(@Param("client") Client client);

    @Query("SELECT DISTINCT cm.chat.id FROM ChatMember cm WHERE cm.employee = :employee")
    List<Long> findChatsByEmployee(@Param("employee") Employee employee);

    // Get all Clients for a specific chat ID
    @Query("SELECT cm.client FROM ChatMember cm WHERE cm.chat.id = :chatId AND cm.client IS NOT NULL")
    List<Client> findClientsByChatId(@Param("chatId") Long chatId);

    // Get all Employees for a specific chat ID
    @Query("SELECT cm.employee FROM ChatMember cm WHERE cm.chat.id = :chatId AND cm.employee IS NOT NULL")
    List<Employee> findEmployeesByChatId(@Param("chatId") Long chatId);

    @Modifying
    @Query("DELETE FROM ChatMember cm WHERE cm.chat.id = :chatId AND cm.employee = :employeeId")
    void deleteByChatIdAndEmployeeId(@Param("chatId") Long chatId, @Param("employeeId") Employee employeeId);

    // Delete by chatID and clientID
    @Modifying
    @Query("DELETE FROM ChatMember cm WHERE cm.chat.id = :chatId AND cm.client = :clientId")
    void deleteByChatIdAndClientId(@Param("chatId") Long chatId, @Param("clientId") Client clientId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ChatMember cm WHERE cm.employee = :employee")
    void deleteByEmployee(@Param("employee") Employee employee);

}
