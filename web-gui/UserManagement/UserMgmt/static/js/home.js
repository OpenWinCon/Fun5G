
var app = angular.module('webApp', []);


app.config(['$interpolateProvider', '$httpProvider', function($interpolateProvider, $httpProvider) {
  $interpolateProvider.startSymbol('{[');
  $interpolateProvider.endSymbol(']}')

}]);


app.controller('homeCtrl', ['$rootScope', '$scope', '$http', function($rootScope, $scope, $http) {
  $scope.hideform = true;
  $scope.options = {
    priority:[
      {id:'1', name: '1'},
      {id:'2', name: '2'},
      {id:'3', name: '3'},
      {id:'4', name: '4'},
      {id:'5', name: '5'},
      {id:'6', name: '6'},
      {id:'7', name: '7'},
      {id:'8', name: '8'},
      {id:'9', name: '9'},
      {id:'10', name: '10'}
    ],
    devices:['wired', 'wireless']
  };

  $scope.submit_form = {
    uid: '',
    uname: '',
    priority: '',
    devices:[]
  };

  $scope.getList = function() {
    $http.get("http://interest.snu.ac.kr:8000/api/user/all")
      .success(function(response) {
        $scope.users = response;
      });
  };


  $scope.getList();

  $scope.editUser = function(user) {
    $scope.hideform = false;
    if (user == 'new') {
      $scope.edit = true;
      $scope.incomplete = true;
      $scope.modified = false;

      $scope.submit_form.uid = '';
      $scope.submit_form.uname = '';
      $scope.submit_form.priority = '';
      $scope.submit_form.devices = [];

    } else {
      $scope.edit = false;
      $scope.modified = true;

      $scope.submit_form.uid = user.uid;
      $scope.submit_form.uname = user.uname;
      $scope.submit_form.priority = user.priority.toString();
      $scope.submit_form.devices = angular.copy(user.devices);
    }

  };

  $scope.delUser = function(user) {
    var _url = "http://interest.snu.ac.kr:8000/api/user/" + user.uid;
      $http.delete(_url).success(function(response) {
        $scope.getList();
        $scope.hideform = true;
      });
  };

  // toggle selection for a given employee by name
  $scope.toggleSelection = function toggleSelection(deviceName) {
     var idx = $scope.submit_form.devices.indexOf(deviceName);
     // is currently selected
     if (idx > -1) {
       $scope.submit_form.devices.splice(idx, 1);
     }
     // is newly selected
     else {
       $scope.submit_form.devices.push(deviceName);
     }
   };

  $scope.refresh = function() {
    $http.get("http://interest.snu.ac.kr:8000/api/user/all")
      .success(function(response) {
        $scope.users = response;
      });
  };

  $scope.submit = function() {
    var _url = "http://interest.snu.ac.kr:8000/api/user/" + $scope.submit_form.uid;
    var req = {
      method:'POST',
      url:_url,
      headers:{'Content-Type':undefined},
      data:$scope.submit_form
    };

    if ($scope.modified) {
      req.method = 'PUT'; // temporary!
      $http(req).success(function(response) {
        $scope.getList();
        $scope.hideform = true;
      });
    } else {
      $http(req).success(function(response) {
        $scope.getList();
        $scope.hideform = true;
      });
    }
  };


  $scope.$watch('submit_form.uid', function() {$scope.test();});
  $scope.$watch('submit_form.uname', function() {$scope.test();});
  $scope.$watch('submit_form.priority', function() {$scope.test();});
  $scope.$watch('submit_form.devices', function() {$scope.test();});

  $scope.test = function() {
    $scope.incomplete = true;
    if ($scope.submit_form.uid !== "" && 
        $scope.submit_form.uname !== "" && 
        $scope.submit_form.priority !== "" /*&& 
        $scope.udevices.length */) {
      $scope.incomplete = false;
    }
  };


}]);



