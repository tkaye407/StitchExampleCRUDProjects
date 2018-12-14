
function findOne() {
	const filterDoc = {quantity: {$gte: 25}};

	itemsCollection.find(filterDoc, {limit: 1}).first().then(result => {
		if (result) {
			console.log("Successfully Found Document: " + JSON.stringify(result));
		} else {
			console.log("No matching document found");
		}
	}, (error) => {
		alert("Error Finding Document: " + error);
	});
}

function findMany() {
	const filterDoc = {"reviews.0": {$exists: true}};
	const optionsDoc = {projection: {_id: 0}, sort: {name: 1}};

	itemsCollection.find(filterDoc, optionsDoc).asArray().then(results => {
		console.log("Successfully found " + results.length + " items");
		for (let i in results) {
			console.log(i + ": " + JSON.stringify(results[i]));
		}
	}, (error) => {
		alert("Error Finding Documents: " + error);
	});
}