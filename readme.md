## Awesome Pizza Slice
To run the project, just run the following commands (change between docker/podman based on your preferences):
```
./mvnw spring-boot:build-image
docker compose build
docker compose up
```
in the root directory of the project.

The project will be available at `http://localhost:8000`.

The project has a main endpoint `api/orders` that receives a JSON payload with the following structure:
```json
{
  "pizzaType": "MARGHERITA",
  "quantity": 1
}
```
With a POST request to the endpoint `api/orders` the user can create a new order and receives back the order code.

With a GET request to the endpoint `api/orders/{orderCode}/status` the user can check the status of an order given its code.

With a PATCH request to the endpoint `api/orders/{orderCode}/status` the chef can update the status of a specific order.

With a GET request to the endpoint `api/orders/queue` the chef can retrieve the list of orders, sorted by the first received order.

With a GET request to the endpoint `api/orders/next` the chef can retrieve the nex order, which will one of the following:

- If there is an order in PREPARING status, that order will be retrieved;
- If no order is in PREPARING status, the first received order will be retrieved;
- If no order has been received, then the endpoint will return null.