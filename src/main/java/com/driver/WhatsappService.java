package com.driver;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WhatsappService {

    WhatsappRepository repository = new WhatsappRepository();

    //1
    public String createUser(String name, String mobile) throws Exception{
        repository.createUser(name,mobile);
        return "SUCCESS";
    }

    //2
    public Group createGroup(List<User> users){
        return repository.createGroup(users);
    }

    //3
    public int createMessage(String content){
        return repository.createMessage(content);
    }

    //4
    public int sendMessage(Message message, User sender, Group group) throws Exception{
        return repository.sendMessage(message,sender,group);
    }

    //5
    public String changeAdmin(User approver, User user, Group group) throws Exception{
        repository.changeAdmin(approver, user, group);
        return "SUCCESS";
    }

    //6
    public int removeUser(User user) throws Exception{
        return 0;
    }

    //7
    public String findMessage(Date start, Date end, int K) throws Exception{
        return null;
    }
}