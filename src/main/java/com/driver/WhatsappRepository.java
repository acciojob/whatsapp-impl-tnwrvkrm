package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;


    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<>();
        this.groupUserMap = new HashMap<>();
        this.senderMap = new HashMap<>();
        this.adminMap = new HashMap<>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public Set<Message> getListOfMessageOfUser(User user){
        Set<Message> msgSet = new HashSet<>();
        for(Message message:senderMap.keySet()){
            if(senderMap.get(message)==user){
                msgSet.add(message);
            }
        }
        return msgSet;
    }

    public User getUser(User someUser,Group myGroup){

        for(User user:groupUserMap.get(myGroup)){
            if(user.getName().equals(someUser.getName()) && user.getMobile().equals(someUser.getMobile())){
                return user;
            }
        }
        return null;
    }

    public boolean checkIfUserIsAnAdmin(User user){
        for(Group group:adminMap.keySet()){
            if(adminMap.get(group).getName().equals(user.getName()) && adminMap.get(group).getMobile().equals(user.getMobile())){
                return true;
            }
        }
        return false;
    }

    public Group getGroup(Group group){

        for(Group someGroup:groupUserMap.keySet()){
            if(someGroup.getName().equals(group.getName()) && someGroup.getNumberOfParticipants()==group.getNumberOfParticipants()){
                return someGroup;
            }
        }
        return null;
    }

    public boolean checkIfUserExistInGroup(User sender,Group group){
        for(User user : groupUserMap.get(group)){
            if(user.getName().equals(sender.getName()) && user.getMobile().equals(sender.getMobile())){
                return true;
            }
        }
        return false;
    }
    public void createUser(String name,String mobNo) throws Exception{

        if(userMobile.contains(mobNo)){
            throw new Exception("User already exists");
        }
        else{
            User user = new User(name,mobNo);
            userMobile.add(mobNo);

        }

    }

    public Group createGroup(List<User> users){

        String groupName = "";
        int numOfParticipants = users.size();
        User admin= users.get(0);

        if(users.size() > 2){
            customGroupCount++;
            groupName="Group " + customGroupCount;
        }
        else{
            groupName = users.get(1).getName();
        }

        Group group = new Group(groupName,numOfParticipants);
        groupUserMap.put(group,users);
        adminMap.put(group,admin);
        groupMessageMap.put(group,new ArrayList<>());

        return group;
    }

    public int createMessage(String content){
        messageId++;
        Message msg = new Message(messageId,content);
        return messageId;
    }

    public int sendMessage(Message message,User sender,Group group) throws Exception{
        int numOfMessages =0;
        Group myGroup = getGroup(group);

        if(myGroup == null){
            throw new Exception("Group does not exist");
        }
        else if(!checkIfUserExistInGroup(sender,myGroup)){
            throw new Exception("You are not allowed to send message");
        }
        else {
            groupMessageMap.get(myGroup).add(message);
            senderMap.put(message,sender);
            numOfMessages = groupMessageMap.get(myGroup).size();
        }

        return numOfMessages;
    }

    public void changeAdmin(User approver, User user, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.
        Group myGroup = getGroup(group);
        //group does not exist
        if(myGroup==null){
            throw new Exception("Group does not exist");
        }

        else if(!(adminMap.get(myGroup).getMobile().equals(approver.getMobile()))){
            throw new Exception("Approver does not have rights");
        }
        else if(!checkIfUserExistInGroup(user,myGroup)){
            throw new Exception("User is not a participant");
        }

        groupUserMap.get(myGroup).remove(getUser(user, group));
        groupUserMap.get(myGroup).add(0,user);
        adminMap.put(myGroup,user);
    }
}