using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace p3
{
    class Program
    {
        static void Main(string[] args)
        {
            var factory = new ConnectionFactory() { HostName = "localhost" };
            Bitmap originale;
            Bitmap resize1;
            Bitmap resize2;
            using (var connection = factory.CreateConnection())
            using (var channel = connection.CreateModel())
            {
                channel.ExchangeDeclare(exchange: "fromP1", type: "topic");
                var queueName = channel.QueueDeclare().QueueName;


                channel.QueueBind(queue: queueName,
                                  exchange: "fromP1",
                                  routingKey: "image");


                Console.WriteLine(" [*] Waiting for messages. To exit press CTRL+C");

                var consumer = new EventingBasicConsumer(channel);
                consumer.Received += (model, ea) =>
                {

                    Console.WriteLine($"Reception en cours sur le topic {ea.Exchange} : {ea.RoutingKey}");

                    var body = ea.Body;

                    using (var ms = new MemoryStream(body))
                    {
                        originale = new Bitmap(ms);
                        resize1 = new Bitmap(originale, new Size(100, 100));
                        resize2 = new Bitmap(originale, new Size(250, 250));
                    }

                    using (var channel2 = connection.CreateModel())
                    {
                        channel2.ExchangeDeclare(exchange: "toP4",
                                    type: "topic");

                        var routingKey = "image";

                        byte[] img;


                        using (var ms = new MemoryStream())
                        {
                            originale.Save(ms, ImageFormat.Jpeg);
                            img = ms.ToArray();
                            channel.BasicPublish(exchange: "toP4",
                                             routingKey: routingKey,
                                             basicProperties: null,
                                             body: img);
                        }

                        using (var ms = new MemoryStream())
                        {
                            resize1.Save(ms, ImageFormat.Jpeg);
                            img = ms.ToArray();
                            channel.BasicPublish(exchange: "toP4",
                                            routingKey: routingKey,
                                            basicProperties: null,
                                            body: img);
                        }

                        using (var ms = new MemoryStream())
                        {
                            resize2.Save(ms, ImageFormat.Jpeg);
                            img = ms.ToArray();
                            channel.BasicPublish(exchange: "toP4",
                                            routingKey: routingKey,
                                            basicProperties: null,
                                            body: img);
                        }

                        Console.WriteLine(" [x] Sent ");
                        
                    }
                };
                channel.BasicConsume(queue: queueName,
                                     autoAck: true,
                                     consumer: consumer);
                Console.ReadLine();

            }


        }
    }
}
