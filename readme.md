# place.order

This project demonstrates a basic order placement flow for a modern e-commerce system, following Test Driven Development (TDD) principles.

## Purpose

place.order focuses on these core aspects:
- Modeling of Order and OrderLine entities
- Stock control and deduction during order placement
- Idempotency (protection against duplicate requests)
- Outbox pattern for event recording (OrderPlaced)
- Extensive unit tests for core order logic

## Features

- **Order Placement:** Create new orders with PlaceOrderCommand, specifying products and quantities.
- **Idempotency:** Prevents duplicate orders for the same idempotency key.
- **Stock Management:** Checks and deducts stock for each order. Throws error if stock is insufficient.
- **OrderLine Tracking:** Tracks product name, quantity, price, and total for each line in an order.
- **Outbox Pattern:** After successful creation, emits an “OrderPlaced” OutboxEvent (supports event-driven or external system integration).
- **Order Status:** Orders are created with a CREATED status.
- **TDD:** Core order logic is thoroughly tested via unit tests.

## Getting Started

Clone the repository and install dependencies:
```bash
git clone https://github.com/Yakup-SENATES/place.order.git
cd place.order
./mvnw clean install
```

## Running Tests

To run all tests:
```bash
./mvnw test
```

## Usage

- Send a placeOrder command with product and quantity information to create an order.
- The service checks stock levels and will not create an order if stock is insufficient.
- On success, an OutboxEvent of type "OrderPlaced" is recorded.
- Duplicate order requests with the same idempotency key are safely ignored.

## Contributing

Feel free to fork the repo and open pull requests.
- Add new test scenarios and edge cases
- Improve or extend the outbox event flow
- Expand order details

## License

MIT License

---

Feedback and contributions are always welcome!