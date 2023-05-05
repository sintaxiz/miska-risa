rsconf = {
    _id : "replicas",
    members: [
        {
            "_id": 0,
            "host": "mongodb-1:27017",
            "priority": 3
        },
        {
            "_id": 1,
            "host": "mongodb-2:27017",
            "priority": 2
        },
        {
            "_id": 2,
            "host": "mongodb-3:27017",
            "priority": 1
        }
    ]
}

rs.initiate(rsconf);