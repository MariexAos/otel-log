sh mqadmin sendMessage -n rmqnamesrv:9876 -t TCP_MESSAGE_TOPIC -p '{
  "messageId": "123456789",
  "senderId": "test1,test2",
  "receiverId": "001",
  "content": "direct Message!",
  "extraData": "{\"key\":\"value\"}",
  "createTime": "2025-03-12 10:15:30",
  "offline": false
}'


sh mqadmin sendMessage -n rmqnamesrv:9876 -t TCP_MESSAGE_TOPIC -p '{
  "messageId": "123456789",
  "senderId": "test1",
  "receiverId": "001",
  "content": "001-test1 Message!",
  "extraData": "{\"key\":\"value\"}",
  "createTime": "2025-03-12 10:15:30",
  "offline": false
}'


sh mqadmin sendMessage -n rmqnamesrv:9876 -t TCP_MESSAGE_TOPIC -p '{
  "messageId": "123456789",
  "senderId": "test1",
  "receiverId": "",
  "content": "001-test1 Message!",
  "extraData": "{\"key\":\"value\"}",
  "createTime": "2025-03-12 10:15:30",
  "offline": false
}'

sh mqadmin sendMessage -n rmqnamesrv:9876 -t TCP_MESSAGE_TOPIC -p '{
  "messageId": "123456789",
  "senderId": "test1",
  "receiverId": "all_by_sender",
  "content": "test1 Message!",
  "extraData": "{\"key\":\"value\"}",
  "createTime": "2025-03-12 10:15:30",
  "offline": false
}'