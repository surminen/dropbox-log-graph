angular.module('hello', [])
  .controller('home', function($scope, $http) {
  $http.get('/demo/').success(function(data) {
    $scope.greeting = data;
  })
});