db = db.getSiblingDB("items");
 
db.createCollection("item");

db = db.getSiblingDB("admin");

db.createUser({
	user: "item",
	pwd: process.env.MONGO_INITDB_PASSWORD,
	roles: [{ role: "readWrite", db: "items" }]
});

