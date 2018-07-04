angular
  .module('weightsList')
  .component('weightsList', {
    templateUrl: '/assets/javascripts/weights-list/weights-list.template.html',
    controller: function() {
      this.weights = [{
        date: 'date #1',
        weight: 'weight #1',
        fat: 'fat #1',
        water: 'water #1',
        muscle: 'muscle #1'
      }, {
        date: 'date #2',
        weight: 'weight #2',
        fat: 'fat #2',
        water: 'water #2',
        muscle: 'muscle #2'
      }, {
        date: 'date #3',
        weight: 'weight #3',
        fat: 'fat #3',
        water: 'water #3',
        muscle: 'muscle #3'
      }, {
        date: 'date #4',
        weight: 'weight #4',
        fat: 'fat #4',
        water: 'water #4',
        muscle: 'muscle #4'
      }];
    }
  });
