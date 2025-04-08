sh mqadmin sendMessage -n rmqnamesrv:9876 -t TCP_MESSAGE_TOPIC -p '{
  "messageId": "123456789",
  "senderId": "test1,test2",
  "receiverId": "001",
  "content": "direct Message!",
  "extraData": "{\"key\":\"value\"}",
  "createTime": "2025-03-12 10:15:30",
  "offline": false
}'


sh mqadmin sendMessage -n 127.0.0.1:9876 -t TCP_MESSAGE_TOPIC -p '{
  "messageId": "123456789",
  "senderId": "test1,test2",
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

sh mqadmin sendMessage -n 127.0.0.1:9876 -t TCP_MESSAGE_TOPIC -p '{
  "messageId": "123456789",
  "senderId": "test1",
  "receiverId": "all_by_sender",
  "content": "test1 Message!",
  "extraData": "{\"key\":\"value\"}",
  "createTime": "2025-03-12 10:15:30",
  "offline": false
}'


helm install higress -n higress-system higress.io/higress --create-namespace --render-subchart-notes --set global.local=true --set global.o11y.enabled=false --set higress-core.gateway.httpPort=18080 --set higress-core.gateway.httpsPort=18443
