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

//    private HashMap<String,Integer> groups;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
//        this.groups=new HashMap<>();
    }
    public Set<Message> getListOfMessageOfAUser(User user){
        Set<Message> msgSet = new HashSet<>();
        for(Message message:senderMap.keySet()){
            if(senderMap.get(message)==user){
                msgSet.add(message);
            }
        }
        return msgSet;
    }
    //returns the reference of the desired user
    public User getUser(User someUser,Group myGroup){

        for(User user:groupUserMap.get(myGroup)){
            if(user.getName().equals(someUser.getName()) && user.getMobile().equals(someUser.getMobile())){
                return user;
            }
        }
        return null;
    }

    //checks if user is an admin or not
    public boolean checkIfUserIsAnAdmin(User user){
        for(Group group:adminMap.keySet()){
            if(adminMap.get(group).getName().equals(user.getName()) && adminMap.get(group).getMobile().equals(user.getMobile())){
                return true;
            }
        }
        return false;
    }

    //returns the group objects address if group exists
    //else it returns null
    public Group getGroup(Group group){

        for(Group someGroup:groupUserMap.keySet()){
            if(someGroup.getName().equals(group.getName()) && someGroup.getNumberOfParticipants()==group.getNumberOfParticipants()){
                return someGroup;
            }
        }
        return null;
    }

    public boolean checkIfUserExistInGroup(User sender,Group group){
        for(User user:groupUserMap.get(group)){
            if(user.getName().equals(sender.getName()) && user.getMobile().equals(sender.getMobile())){
                return true;
            }
        }
        return false;
    }
    public void createUser(String name,String mobno) throws Exception{

        if(userMobile.contains(mobno)){
            throw new Exception("User already exists");
        }
        else{
            User user = new User(name,mobno);
            userMobile.add(mobno);

        }

    }

    public Group createGroup(List<User> users){

        String groupName = "";
        int numOfParticipants=users.size();
        User admin= users.get(0); //1st user in users list is admin

        if(users.size()>2){
            ++customGroupCount;
            groupName="Group "+customGroupCount;
        }
        else{
            groupName=users.get(1).getName(); //group name is name of 2nd user
        }
        Group group = new Group(groupName,numOfParticipants);
        groupUserMap.put(group,users);
        adminMap.put(group,admin);
        groupMessageMap.put(group,new ArrayList<>());
//        groups.put(groupName,groups.getOrDefault(groupName,0)+1);
        return group;
    }

    public int createMessage(String content){
        ++messageId;
        Message msg = new Message(messageId,content);
        return messageId;
    }

    public int sendMesage(Message message,User sender,Group group) throws Exception{
        int numOfMessages =0;
        Group myGroup = getGroup(group);
        //group does not exist
        if(myGroup==null){
            throw new Exception("Group does not exist");
        }
        else if(!checkIfUserExistInGroup(sender,myGroup)){
            throw new Exception("You are not allowed to send message");
        }
        else {
            groupMessageMap.get(myGroup).add(message);
            senderMap.put(message,sender);
            numOfMessages=groupMessageMap.get(myGroup).size();
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

        groupUserMap.get(myGroup).remove(getUser(user,myGroup)); //remove the user from the group
        groupUserMap.get(myGroup).add(0,user); //add user at index 0 making him/her the admin
        adminMap.put(myGroup,user);
    }

    public int removeUser(User user) throws Exception{
        //This is a bonus problem and does not contains any marks
        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)

        User myUser=null;
        Group usersGroup=null;
        boolean isUserFound=false;
        for(Group group:groupUserMap.keySet()){
            myUser = getUser(user,group);
            if(myUser!=null){
                isUserFound=true;
                break;
            }
        }
        if(!isUserFound){
            throw new Exception("User not found");
        }
        if(checkIfUserIsAnAdmin(myUser)){
            throw new Exception("Cannot remove admin");
        }
        //getting the group the user belongs to
        for(Group g:groupUserMap.keySet()){
            if(checkIfUserExistInGroup(myUser,g)){
                usersGroup=g;
                break;
            }
        }
        groupUserMap.get(usersGroup).remove(myUser);
        Set<Message> messageSetOfUser = getListOfMessageOfAUser(myUser);
        //remove messages of myuser from all db's
        senderMap.keySet().removeAll(messageSetOfUser);
        groupMessageMap.get(usersGroup).removeAll(messageSetOfUser);
        //remove the mobile num of the user from usermobile set
        if(userMobile.contains(myUser.getMobile())){
            userMobile.remove(myUser.getMobile());
        }

        return groupUserMap.get(usersGroup).size()+groupMessageMap.get(usersGroup).size()+senderMap.size();
    }

    public String findMessage(Date start , Date end , int k) throws Exception{
        //This is a bonus problem and does not contains any marks
        // Find the Kth latest message between start and end (excluding start and end)
        // If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception
        PriorityQueue<Message> pq = new PriorityQueue<>((m1,m2)->m2.getTimestamp().compareTo(m1.getTimestamp()));
        String kthLatestMsg="";
        for(Message message:senderMap.keySet()){
            if(message.getTimestamp().compareTo(start)==1 && message.getTimestamp().compareTo(end)==-1){
                pq.add(message);
            }
        }

        if(pq.size()<k){
            throw new Exception("K is greater than the number of messages");
        }
        else{
            while(k-->1){
                pq.poll();
            }
            kthLatestMsg=pq.peek().getContent();
            return kthLatestMsg;
        }
    }


}