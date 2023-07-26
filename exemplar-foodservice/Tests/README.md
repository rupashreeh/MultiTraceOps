Test case 1: 
-------------
Customer 301 ordered all the 10 items from Restaurant 1 with ID 1 and spent 1800 Rs 
Customer 302 tries ordering the same item. The order placement fails this time. 

Test case 2: 
------------
Customer tries to add negative amount in wallet and is denied. 
Customer tries to add amount to incorrect customer ID. 

Test case 3: 
------------
Customer orders items and spends almost all the money with only 200 Rs left. 
Customer tries ordering again and does not have enough money and fails.  
Customer adds more amount to their wallet. 
Places order again and order is successful now.

Test case 4: 
-----------
Agent signs in then signs out. 
Order created but unassigned to agent. 


Test case 5: 
------------
Restaurant adds negative qty to request order. 
The response is GONE.
Restaurant adds negative qty to refill item.
Restaurant adds qty to incorrect restaurant ID.
Restaurant adds qty to incorrect item ID.

Test case 6: 
-------------
Two delivery agents and two orders are placed one after the other. 

Test case 7: 
-------------
Two delivery agents and two orders are placed one after the other by different customers. 

Test case 8: 
-------------
Two delivery agents and two orders are placed one after the other by same customer. 
Both are accepted and delivered.
