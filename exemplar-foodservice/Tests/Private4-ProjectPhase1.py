from http import HTTPStatus
import requests

# Checks if the order is delivered to the customer
# successfully when the delivery agent, stock of
# the requested food item is available and the
# wallet balance is sufficient.


# RESTAURANT SERVICE    : http://localhost:8080
# DELIVERY SERVICE      : http://localhost:8081
# WALLET SERVICE        : http://localhost:8082

def test():
    test_result = 'Pass'

    # Reinitialize Restaurant service
    http_response = requests.post("http://localhost:8080/reInitialize")

    if(http_response.status_code != HTTPStatus.CREATED):
        test_result = 'Fail'

    # Reinitialize Delivery service
    http_response = requests.post("http://localhost:8081/reInitialize")

    if(http_response.status_code != HTTPStatus.CREATED):
        test_result = 'Fail'

    # Reinitialize Wallet service
    http_response = requests.post("http://localhost:8082/reInitialize")

    if(http_response.status_code != HTTPStatus.CREATED):
        test_result = 'Fail'

    # Agent 201 sign in
    http_response = requests.post(
        "http://localhost:8081/agentSignIn", json={"agentId": 201})

    if(http_response.status_code != HTTPStatus.CREATED):
        test_result = 'Fail'

    # Check Agent 201 status
    http_response = requests.get("http://localhost:8081/agent/201")

    if(http_response.status_code != HTTPStatus.OK):
        test_result = 'Fail'
    res_body = http_response.json()

    agent_id = res_body.get("agentId")
    status = res_body.get("status")

    if agent_id != 201 or status != "available":
        test_result = 'Fail'

    # Agent signs out
    http_response = requests.post(
        "http://localhost:8081/agentSignOut", json={"agentId": 201})

    # Check Agent 201 status
    http_response = requests.get("http://localhost:8081/agent/201")

    if(http_response.status_code != HTTPStatus.OK):
        test_result = 'Fail'
    res_body = http_response.json()

    agent_id = res_body.get("agentId")
    status = res_body.get("status")
    if agent_id != 201 or status != "signedout":
        test_result = 'Fail'

    # Customer 301 requests an order of item 1, quantity 1 from restaurant 101
    http_response = requests.post(
        "http://localhost:8081/requestOrder", json={"custId": 301, "restId": 101, "itemId": 1, "qty": 1})

    res_body = http_response.json()
    actual_order_id = res_body.get("orderId")

    # Order succeeded but no agent assigned
    if(http_response.status_code != HTTPStatus.CREATED):
        test_result = 'Fail'
   
    #Get the order and verify agent 201 is not assigned to this order
    http_response = requests.get(f"http://localhost:8081/order/{actual_order_id}")

    if(http_response.status_code != HTTPStatus.OK):
        test_result = 'Fail'

    res_body = http_response.json()
    agent_id = res_body.get("agentId")
    order_status = res_body.get("status")
    actual_order_id = res_body.get("orderId")

    if(agent_id != -1 or order_status != 'unassigned'):
        test_result = 'Fail'

   
    if(agent_id != -1):
        test_result = 'Fail'

    return test_result


if __name__ == "__main__":
    test_result = test()
    print(test_result)
