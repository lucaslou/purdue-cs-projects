import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description A ticket database allowing tickets to be purchased
 * by customers
 * @author jwallrab
 *
 */
public class TicketDatabase implements TicketDB{
    
    private ConcurrentHashMap<Integer,Ticket> availableTickets = new ConcurrentHashMap<Integer,Ticket>();
    private ConcurrentHashMap<Integer, Ticket> reservedTickets = new ConcurrentHashMap<Integer, Ticket>();
    private ConcurrentHashMap<Integer, Ticket> purchasedTickets = new ConcurrentHashMap<Integer, Ticket>();
    private int numTickets = 25;
    private int priceUSDperTicket = 10;
    
    /**
     * Default Constructor
     */
    public TicketDatabase(){
        availableTickets = new ConcurrentHashMap<Integer,Ticket>(numTickets);
        reservedTickets = new ConcurrentHashMap<Integer,Ticket>(numTickets);
        purchasedTickets = new ConcurrentHashMap<Integer,Ticket>(numTickets);
        for(int i = 0; i < numTickets; ++i){
            availableTickets.put(i, new Ticket(i, priceUSDperTicket));
        }
    }
    
    /**
     * Constructor with a programmer defined number of tickets
     * @param numOfTickets
     * @throws Exception
     */
    public TicketDatabase(int numOfTickets) throws Exception{
        setNumTickets(numOfTickets);
        availableTickets = new ConcurrentHashMap<Integer,Ticket>(numTickets);
        reservedTickets = new ConcurrentHashMap<Integer,Ticket>(numTickets);
        purchasedTickets = new ConcurrentHashMap<Integer,Ticket>(numTickets);
        for(int i = 0; i < numOfTickets; ++i){
            availableTickets.put(i, new Ticket(i, priceUSDperTicket));
        }
    }
    
    /**
     * Getter for the number of tickets
     * @return
     */
    public int getNumTickets(){
        return numTickets;
    }
    
    /**
     * Returns an arraylist of the available seat numbers
     * @return
     */
    public ArrayList<Integer> getAvailableSeats(){
        ArrayList<Integer> availableSeats = new ArrayList<Integer>();
        Iterator<Integer> i = availableTickets.keySet().iterator();
        while(i.hasNext()){
            availableSeats.add(i.next());
        }
        Collections.sort(availableSeats);
        return availableSeats;
    }
    
    /**
     * Returns an arraylist of the purchased seat numbers
     * @return
     */
    public ArrayList<Integer> getPurchasedSeats(){
        ArrayList<Integer> purchasedSeats = new ArrayList<Integer>();
        Iterator<Integer> i = purchasedTickets.keySet().iterator();
        while(i.hasNext()){
            purchasedSeats.add(i.next());
        }
        Collections.sort(purchasedSeats);
        return purchasedSeats;
    }
    
    /**
     * Returns an arraylist of the reserved seat numbers
     * @return
     */
    public ArrayList<Integer> getReservedSeats(){
        ArrayList<Integer> reservedSeats = new ArrayList<Integer>();
        Iterator<Integer> i = reservedTickets.keySet().iterator();
        while(i.hasNext()){
            reservedSeats.add(i.next());
        }
        Collections.sort(reservedSeats);
        return reservedSeats;
    }
    
    /**
     * Setter for the number of tickets
     * @param numOfTickets
     * @throws Exception
     */
    public void setNumTickets(int numOfTickets) throws Exception{
        if(numOfTickets < 1)
            throw new Exception("Number of Tickets must be larger than 0!");
        numTickets = numOfTickets;
    }
    
    /**
     * Allows a customer to purchase an available seat number
     * @param seatNum
     * @param c
     * @throws Exception
     */
    public synchronized void purchaseTicket(int seatNum, CustomerData c) throws Exception{
        //TODO
        if (availableTickets.containsKey(seatNum))
        {
            Ticket bought = availableTickets.remove(seatNum); // the purchased ticket
            bought.setOwner(c);
            purchasedTickets.put(seatNum, bought);
        }
        // checks if ticket was reserved by customer and buys it if it was
        else if (reservedTickets.containsKey(seatNum))  
        {
            Ticket reserved = reservedTickets.get(seatNum); // the purchased ticket that was reserved
            if (c.equals(reserved.getOwner()))
            {
                reserved.setOwner(c);
                reservedTickets.remove(seatNum);
                purchasedTickets.put(seatNum, reserved);
            }      
            else 
            {
                throw new Exception("Could not purchase seat.");
            }
        }
        else 
        {
            if (seatNum > numTickets)
                throw new Exception ("Unable to purchase seat " + seatNum);
            else
                throw new Exception ("Unable to purchase seat " + seatNum);
        }
    }
    
    /**
     * Allows a customer to reserve an available seat number
     * @param seatNum
     * @param c
     * @throws Exception
     */
    public synchronized void reserveTicket(int seatNum, CustomerData c) throws Exception{
        //TODO
        // if seat is available, it reserves it
        if (availableTickets.containsKey(seatNum))
        {
            Ticket reserved = availableTickets.remove(seatNum);
            reserved.setOwner(c);
            reservedTickets.put(seatNum, reserved); 
        }
        else 
            throw new Exception();
    }
    
    /**
     * Allows a customer to release a reserved seat number
     * @param seatNum
     * @param c
     * @throws Exception
     */
    public synchronized void releaseTicket(int seatNum, CustomerData c) throws Exception{
        //TODO
        // releases ticket (called after timer expires)
        if (reservedTickets.containsKey(seatNum))
        {
            Ticket available = reservedTickets.remove(seatNum);
            //available.setOwner(null);
            availableTickets.put(seatNum, new Ticket(seatNum, priceUSDperTicket));
        }
        else
            throw new Exception();
    }
    
    /**
     * This prints out a summary of all ticket sales
     */
    public void salesSummary(){
        //TODO
        Iterator<Entry<Integer, Ticket>> it = purchasedTickets.entrySet().iterator();
        
        System.out.println("\n***** SALES SUMMARY ******");
        
        while(it.hasNext()){
            Map.Entry pairs = (Map.Entry) it.next();
            Ticket t = (Ticket) pairs.getValue();
            System.out.println("Seat " + pairs.getKey() + "; "
                                   + t.getOwner().getName() + " " + t.getOwner().getPhoneNumber());
        }
        
        System.out.println("**************************\n");
    }
    
    /**
     * Returns the ticket for customer c or throws an exception
     * @param c
     * @return
     * @throws Exception
     */
    public Ticket getTicketForUser(CustomerData c) throws Exception{
        Iterator<Integer> i = purchasedTickets.keySet().iterator();
        while(i.hasNext()){
            if(purchasedTickets.get(i.next()).getOwner().equals(c))
                return purchasedTickets.get(i);
        }
        throw new Exception("No ticket has been purchased by Customer " + c.getName());
    }
    
    /**
     * Returns the ticket for a given seat number or throws an exception
     * @param seatNum
     * @return
     * @throws Exception
     */
    public Ticket getTicketBySeat(int seatNum) throws Exception{
        if(availableTickets.containsKey(seatNum))
            return availableTickets.get(seatNum);
        if(purchasedTickets.containsKey(seatNum))
            return purchasedTickets.get(seatNum);
        if(reservedTickets.containsKey(seatNum))
            return reservedTickets.get(seatNum);
        throw new Exception("No ticket for seat number " + seatNum);
    }
}
