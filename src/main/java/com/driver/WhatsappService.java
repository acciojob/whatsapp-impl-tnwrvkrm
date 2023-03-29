package com.driver;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WhatsappService {

    WhatsappRepository repository = new WhatsappRepository();

    public String createUser(String name, String mobile) throws Exception{
        repository.createUser(name,mobile);
        return "SUCCESS";
    }
    public Group createGroup(List<User> users){
        return repository.createGroup(users);
    }

    public int createMessage(String content){
        return repository.createMessage(content);
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        return repository.sendMesage(message,sender,group);
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        repository.changeAdmin(approver,user,group);
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception{
        return repository.removeUser(user);
    }

    public String findMessage(Date start, Date end, int K) throws Exception{
        return repository.findMessage(start,end,K);
    }
}