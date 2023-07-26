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
    #http_response = requests.post("http://localhost:8080/reInitialize")

    #if(http_response.status_code != HTTPStatus.CREATED):
    #    test_result = 'Fail'

    # Reinitialize Delivery service
    #http_response = requests.post("http://localhost:8081/reInitialize")

    #if(http_response.status_code != HTTPStatus.CREATED):
    #    test_result = 'Fail'

    # Reinitialize Wallet service
    http_response = requests.post("http://localhost:8082/reInitialize")

    if(http_response.status_code != HTTPStatus.CREATED):
        test_result = 'Fail'

    # Add more money to the wallet
    http_response = requests.post(
        "http://localhost:8082/addBalance", json={"custId": 301, "amount": -500})

    if(http_response.status_code != HTTPStatus.OK):
        print ("-500 dedcuted")
        test_result = 'Fail'

    # Get balance and check if it is still 2000
    http_response = requests.get("http://localhost:8082/balance/301")
    res_body = http_response.json()
    curr_balance = res_body.get("amount")
    if (curr_balance != 2000):
        print ("curr balance is " + str(curr_balance))
        print("curr_balance is not 2000")
        test_result = 'Fail'

    # Add more money to the wallet
    http_response = requests.post(
        "http://localhost:8082/addBalance", json={"custId": 301, "amount": 500})

    if(http_response.status_code != HTTPStatus.OK):
        print ("+500 dedcuted")
        test_result = 'Fail'

    # Get balance and check if it is 2500
    http_response = requests.get("http://localhost:8082/balance/301")
    res_body = http_response.json()
    curr_balance = res_body.get("amount")
    if (curr_balance != 2500):
        print ("curr balance is " + str(curr_balance))
        print("curr_balance is not 2500")
        test_result = 'Fail'

    return test_result


if __name__ == "__main__":
    test_result = test()
    print(test_result)
