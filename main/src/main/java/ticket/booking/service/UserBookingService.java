import ticket.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import ticket.booking.entities.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.File;
import java.nio.file.Paths;
import java.util.*;



public class UserBookingService {
    
    // These are the only things needed to be declared before declaring other classes
    private User user;
    private List<User> userList;
    private static final String USERS_PATH = "src/main/java/ticket/booking/localDB/users.json"; // to alwasys keep this path in the memory
    private ObjectMapper objectMapper = new ObjectMapper();


    // Declaring methods and constructors 
    public UserBookingService(User user1) throws IOException { // creating this for one time login and saving user info

        this.user = user1;
    
        File users = new File(USERS_PATH);
        userList = objectMapper.readValue(users, new TypeReference<List<User>>() {}); // This means userList has been assigned the object processed from ObjectMapper class. the data was fetched from users(USERS_PATH) which was converted to List(list has a generic data type therefore we use 'TypeReference' to let compiler understand geneeric type of the List)
    }
    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }
    private void loadUserListFromFile() throws IOException {
        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<List<User>>() {});
    }


    // related methods
    public Boolean loginUser() {

        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.gerName()) && UserserviceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();  
    }
    public Boolean signUp(User user1){
     
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }


    private void saveUserListToFile() throws IOException {
        
        File usersFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBookings(){
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        if(userFetched.isPresent()){
            userFetched.get().printTickets();
        }
    }

    public Boolean cancelBooking(String ticketId){
        if(ticketId==null || ticketId.isEmpty()){
            return false;
        }
        boolean removed = user.getTicketsBooked()
                .removeIf(ticket -> ticket.getTicketId().equals(ticketId));

        return removed;
    }


    public List<Train> getTrains(String source, String destination){
        
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){ // List of list of Intgers arguments of type Train 
        
        return train.getSeats();
    }


    // make an object TrainService
     public Boolean bookTrainSeat(Train train, int row, int seat) {
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true; // Booking successful
                } else {
                    return false; // Seat is already booked
                }
            } else {
                return false; // Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

}
 
