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

    # Add negative qty of item
    http_response = requests.post(
        "http://localhost:8080/refillItem", json={"restId": 101, "itemId": 1, "qty":-5})

    if(http_response.status_code == HTTPStatus.OK):
        test_result = 'Fail'

   # Add negative qty of item
    http_response = requests.post(
        "http://localhost:8080/acceptOrder", json={"restId": 101, "itemId": 1, "qty":-5})

    if(http_response.status_code == HTTPStatus.OK):
        test_result = 'Fail'

    # Add qty to wrong restaurant ID
    http_response = requests.post(
        "http://localhost:8080/refillItem", json={"restId": 105, "itemId": 1, "qty":5})

    if(http_response.status_code == HTTPStatus.OK):
        test_result = 'Fail'

    # Add qty to wrong item ID
    http_response = requests.post(
        "http://localhost:8080/acceptOrder", json={"restId": 101, "itemId": 6, "qty":5})

    if(http_response.status_code == HTTPStatus.OK):
        test_result = 'Fail'


    return test_result

if __name__ == "__main__":
    test_result = test()
    print(test_result)
