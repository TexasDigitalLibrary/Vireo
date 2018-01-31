vireo.filter('trusted', function($sce) {
    return function (htmlString) {
        return $sce.trustAsHtml(htmlString);
    };
});