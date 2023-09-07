import pika

cloudamqp_host = "rattlesnake-01.rmq.cloudamqp.com"
cloudamqp_port = 5672  # Default RabbitMQ port
cloudamqp_username = " 	ajvnaswz"
cloudamqp_password = "DpQvHECpdCzQUZobhKTgfWIPUzYWsUwn"
cloudamqp_vhost = "ajvnaswz"  # Default virtual host ("/" for the default vhost)

def make_rabbitmq_message(message):
    credentials = pika.PlainCredentials('administrator', 'Kairox2021Admin@')
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='208.51.61.144', port=5672,
                                                                   credentials=credentials))
    #connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='task_queue', durable=True)
    channel.basic_publish(
        exchange='',
        routing_key="task_queue",
        body=json.dumps(message),
        properties=pika.BasicProperties(
            delivery_mode=pika.spec.PERSISTENT_DELIVERY_MODE
        ))
    print(" [x] Sent %r" % message)
    connection.close()

credentials = pika.PlainCredentials(cloudamqp_username, cloudamqp_password)
parameters = pika.ConnectionParameters(host=cloudamqp_host,
                                      port=cloudamqp_port,
                                      virtual_host=cloudamqp_vhost,
                                      credentials=credentials)
connection = pika.BlockingConnection(parameters)
channel = connection.channel()

# Declare the queue you want to interact with
queue_name = "your_queue_name"
channel.queue_declare(queue=queue_name)

# Publish a message to the queue
message = "Hello, CloudAMQP!"
channel.basic_publish(exchange='',
                      routing_key=queue_name,
                      body=message)

print(f"Sent: {message}")

# Close the connection when done
connection.close()