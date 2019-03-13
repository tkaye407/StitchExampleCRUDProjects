
function aggregate() {
	const aggregationPipeline = [
		{$group: {
			_id: "$category", 
			totalQuantity: {$sum: "$quantity"}, 
			count: {$sum: 1}
		}}
	]

	itemsCollection.aggregate(aggregationPipeline).asArray().then(results => {
		console.log("Successfully found " + results.length + " aggregation items");
		for (let i in results) {
			console.log(i + ": " + JSON.stringify(results[i]));
		}
	}, (error) => {
		alert("Error Performing Aggregation: " + error);
	});

}