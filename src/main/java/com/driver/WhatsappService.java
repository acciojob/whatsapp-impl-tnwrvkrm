package com.driver;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WhatsappService {

    WhatsappRepository whatsappRepository = new WhatsappRepository();
    Message message;
    public String createUser(String name, String mobileNo){
        try {
            whatsappRepository.getUserMobile().add(mobileNo);
            User user = new User(name, mobileNo);
        }
        catch (Exception e){
            throw new RuntimeException("User already exists");
        }

        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        Group group;

        if(users.size() == 2){
            group = new Group(users.get(1).getName(), 2);
        }
        else{
            whatsappRepository.setCustomGroupCount(whatsappRepository.getCustomGroupCount()+1);

            String groupName = "Group ";
            groupName += String.valueOf(whatsappRepository.getCustomGroupCount());

            group = new Group(groupName, users.size());

            }

        whatsappRepository.getGroupUserMap().put(group, users);
        whatsappRepository.getAdminMap().put(group, users.get(0));
        whatsappRepository.getGroupMessageMap().put(group, new ArrayList<Message>());

        return group;
    }

    public int createMessage(String content){
        message = new Message(content);
        return whatsappRepository.getMessageId();
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!whatsappRepository.getGroupUserMap().containsKey(group)){
            throw new RuntimeException("Group does not exist");
        }
        if(!whatsappRepository.getGroupUserMap().get(group).contains(sender)){
            throw new RuntimeException("You are not allowed to send message");
        }

        whatsappRepository.getGroupMessageMap().get(group).add(message);
        whatsappRepository.getSenderMap().put(message, sender);

        return whatsappRepository.getGroupMessageMap().get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(!whatsappRepository.getGroupUserMap().containsKey(group)){
            throw new RuntimeException("Group does not exist");
        }
        if(!whatsappRepository.getAdminMap().get(group).equals(approver)){
            throw new RuntimeException("Approver does not have rights");
        }
        if(!whatsappRepository.getGroupUserMap().get(group).contains(user)){
            throw new RuntimeException("User is not a participant");
        }

        whatsappRepository.getAdminMap().put(group, user);

        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception{

        return 0;
    }

    public String findMessage(java.util.Date start, Date end, int K) throws Exception{

        return null;
    }
}
