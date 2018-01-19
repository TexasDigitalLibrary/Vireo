vireo.directive("approvalblock", function() {
    return {
        templateUrl: function(element, attr) {
            return "views/directives/approvalBlock.html";
        },
        scope: {
 			type: "=",
			approvalProxy: "=",
			status: "=",
			statusDate: "="
        }
    };
});
