import java.util.*;

class Node{
    String key;
    int value;
    Node next;

    Node(String k,int v){
        key=k;
        value=v;
    }
}

class CustomHashTable{

    int SIZE = 10007;
    Node[] table = new Node[SIZE];

    int hash(String key){
        int h=0;
        for(int i=0;i<key.length();i++)
            h=(31*h+key.charAt(i))%SIZE;
        return h;
    }

    void put(String key,int value){
        int index=hash(key);

        Node head=table[index];
        Node curr=head;

        while(curr!=null){
            if(curr.key.equals(key)){
                curr.value=value;
                return;
            }
            curr=curr.next;
        }

        Node newNode=new Node(key,value);
        newNode.next=head;
        table[index]=newNode;
    }

    boolean contains(String key){
        int index=hash(key);
        Node curr=table[index];

        while(curr!=null){
            if(curr.key.equals(key))
                return true;
            curr=curr.next;
        }
        return false;
    }

    int get(String key){
        int index=hash(key);
        Node curr=table[index];

        while(curr!=null){
            if(curr.key.equals(key))
                return curr.value;
            curr=curr.next;
        }
        return 0;
    }

    void increment(String key){
        int index=hash(key);
        Node curr=table[index];

        while(curr!=null){
            if(curr.key.equals(key)){
                curr.value++;
                return;
            }
            curr=curr.next;
        }

        Node newNode=new Node(key,1);
        newNode.next=table[index];
        table[index]=newNode;
    }

    String getMaxKey(){
        String res="";
        int max=0;

        for(int i=0;i<SIZE;i++){
            Node curr=table[i];
            while(curr!=null){
                if(curr.value>max){
                    max=curr.value;
                    res=curr.key;
                }
                curr=curr.next;
            }
        }
        return res+" ("+max+" attempts)";
    }
}

public class UsernameChecker{

    static CustomHashTable users=new CustomHashTable();
    static CustomHashTable attempts=new CustomHashTable();

    static boolean checkAvailability(String username){
        attempts.increment(username);
        return !users.contains(username);
    }

    static List<String> suggestAlternatives(String username){
        List<String> list=new ArrayList<>();

        for(int i=1;i<=5;i++){
            String candidate=username+i;
            if(!users.contains(candidate))
                list.add(candidate);
        }

        String dot=username.replace('_','.');
        if(!users.contains(dot))
            list.add(dot);

        return list;
    }

    static String getMostAttempted(){
        return attempts.getMaxKey();
    }

    public static void main(String[] args){

        users.put("john_doe",101);
        users.put("admin",1);

        System.out.println(checkAvailability("john_doe"));
        System.out.println(checkAvailability("jane_smith"));

        System.out.println(suggestAlternatives("john_doe"));

        System.out.println(getMostAttempted());
    }
}