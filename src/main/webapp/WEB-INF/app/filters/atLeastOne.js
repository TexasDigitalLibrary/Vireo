vireo.filter('atLeastOne', function () {

    return function (array, protoMember) {

        if (array) {
            array = array.length !== 0 ? array : array.push(protoMember);
        }

        return array;

    };

});
