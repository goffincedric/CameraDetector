# Camera Detector

**Warning**: Don't let the simulator keep sending messages with images. The Processor uses an online image recognition API which only has a limited amount of API calls each month (~500 remaining) for this month.

If you accidentally kept sending images over MQTT:
1) Start the processor
2) Let it receive the messages
3) Quickly stop processor before it can process the received messages

If you are not comfortable doing this, contact me via Canvas or by email (cedric.goffin@student.kdg.be or goffin.cedric@hotmail.com) so I can purge all the messages in the MQTT queue.

Thanks in advance!
