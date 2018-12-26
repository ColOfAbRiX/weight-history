angular
  .module('weightsListModule')
  .component('weightsList', {
    templateUrl: '/assets/javascripts/weights-list/weights-list.template.html',
    controller: function($scope, $http) {
      let self = this;

      /*
       Edit or modify a weight
       */
      $scope.submitUpdateOrDelete = function(action, currentWeight) {
        console.log("submitUpdateOrDelete");
        console.log(currentWeight);
      };

      /*
       Add a new weight
       */
      $scope.submitNew= function(newWeight) {
        console.log("submitNew");
        console.log(newWeight);
      };

      /*
       Load the weights from the API
       */
      $http.get('/api/weight/all')
        .then(function(response) {
          if( response.data.result === "success" ) {
            // In case of success, extract the response data
            self.weights = response.data.data.map( item => {
              item.date = new Date(item.date);
              return item;
            });
            console.log(self.weights);
          }
          else {
            // In case of failure, display the error
            console.error('Error occurred on the processing: ', response.data.message);
          }
        })
        .catch(function(response) {
          // Error at HTTP level, just show what happened
          console.error('Error occurred on the API call:', response.status, response.data);
        })
        .finally(function() {
          // Debugging message
          console.info('API processing terminated');
        });

    }
  });
