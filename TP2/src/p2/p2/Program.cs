using Google.Apis.Services;
using Google.Apis.Translate.v2.Data;
using Google.Cloud.Translation.V2;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text;
using System.Threading.Tasks;

namespace p2
{
    class Program
    {
        private const string GOOGLE_CREDENTIAL = "GOOGLE_APPLICATION_CREDENTIALS";
        static void Main(string[] args)
        {
            bool isReceptionOver = false;
            string texteATraduire = "";
            texteATraduire = "Hello";
            // Cas idéal : on arrive à faire fonctionner la traduction :)
            string translated = Program.Traduction(texteATraduire);
            var factory = new ConnectionFactory() { HostName = "localhost" };
            using (var connection = factory.CreateConnection())
            using (var channel = connection.CreateModel())
            {

                channel.ExchangeDeclare(exchange: "fromP1", type: "topic");
                var queueName = channel.QueueDeclare().QueueName;


                channel.QueueBind(queue: queueName,
                                  exchange: "fromP1",
                                  routingKey: "text");


                Console.WriteLine(" [*] Waiting for messages. To exit press CTRL+C");

                var consumer = new EventingBasicConsumer(channel);
                consumer.Received += (model, ea) =>
                {
                    var paragraphe = Encoding.UTF8.GetString(ea.Body);

                    if(paragraphe == "")
                    {
                        isReceptionOver = true;
                    }
                    else
                    {
                        texteATraduire += paragraphe;
                    }


                    if (isReceptionOver)
                    {
                        
                        // Cas idéal : on arrive à faire fonctionner la traduction :)
                        //string translated = Program.Traduction(texteATraduire);

                        // Cas actuel : on arrive pas à faire fonctionner la traduction

                        //string translated = texteATraduire;

                        using (var channel2 = connection.CreateModel())
                        {
                            channel2.ExchangeDeclare(exchange: "toP4",
                                        type: "topic");

                            var routingKey = "text";

                            List<string> textsToP4 = new List<string>();

                            textsToP4.Add(texteATraduire);
                            textsToP4.Add(translated);


                            using (var ms = new MemoryStream())
                            {
                                var binary = new BinaryFormatter();
                                binary.Serialize(ms, textsToP4);
                                channel.BasicPublish(exchange: "toP4",
                                                routingKey: routingKey,
                                                basicProperties: null,
                                                body: ms.ToArray());
                            }

                            Console.WriteLine(" [x] Sent ");
                        }
                    }


                };
                channel.BasicConsume(queue: queueName,
                                     autoAck: true,
                                     consumer: consumer);




            }
            Console.ReadLine();
        }

        private static string Traduction(string texteToTranslate)
        {
            string translated = "";

            string envVar = Environment.GetEnvironmentVariable(GOOGLE_CREDENTIAL);

            var path = new DirectoryInfo("../../../../../../").FullName;
            path += "Tp2SystDistrBosseBiras-adbae7cf74c7.json";

            if (envVar == null)
            {
                Environment.SetEnvironmentVariable(GOOGLE_CREDENTIAL, path);
            }


            Console.OutputEncoding = System.Text.Encoding.Unicode;
            TranslationClient client = TranslationClient.Create();
            translated = client.TranslateText("Hello World.", "fr", "en").TranslatedText;

            return translated;
        }

    }
}
