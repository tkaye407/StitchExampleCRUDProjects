
function insertOne() {
	const doc = {
		name: "legos", 
		quantity: 10,
		category: "toys", 
		reviews: [{username: "mongolover", comment: "this is great"}]
	};

	itemsCollection.insertOne(doc).then(result => {
		console.log("Successfully Inserted Item with id: " + result.insertedId);
	}, (error) => {
	    alert("Error Inserting Document: " + error);
	});
}

function insertMany() {
	var docs = []
	var times = 1000;
	for(var i=0; i < times; i++){
	     docs.push({
			name: "legos", 
			quantity: 10,
			category: "toys", 
			reviews: [{username: "mongolover", comment: "this is great"}]
		})
	}

	itemsCollection.insertMany(docs).then(result => {
		const ids = Object.keys(result.insertedIds).map(e => result.insertedIds[e])
		console.log("Successfully Inserted " + ids.length + " items with ids: " + ids);	}, (error) => {
	    alert("Error Inserting Document: " + error);
	});
}

function abort() {
	var controller = new AbortController()
	console.log("Calling Abort")
	controller.abort()
	console.log("Called Abort")
}

// function insertMany() {
// 	const doc1 = {name: "basketball", category: "sports", quantity: 20, reviews: [] };
// 	const doc2 = {name: "football",   category: "sports", quantity: 30, reviews: [] };

// 	itemsCollection.insertMany([doc1, doc2]).then(result => {
// 		const ids = Object.keys(result.insertedIds).map(e => result.insertedIds[e])
// 		console.log("Successfully Inserted " + ids.length + " items with ids: " + ids);
// 	}, (error) => {
// 		alert("Error Inserting Documents: " + error);
// 	});
// }