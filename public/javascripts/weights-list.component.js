angular
  .module('weightsHistory')
  .component('weightsList', {
    template: `
      <form ng-repeat="weight in $ctrl.weights">
        <div class="form-row">
          <div class="col-1">
            <label class="sr-only" for="inlineFormInput">Date</label>
            <input type="text" class="form-control mb-2" id="date" value="{{ weight.date }}" placeholder="Date" disabled>
          </div>
          <div class="col-1">
            <label class="sr-only" for="inlineFormInput">Weight (Kg)</label>
            <input type="text" class="form-control mb-2" id="weight" value="{{ weight.weight }}" placeholder="Weight (Kg)">
          </div>
          <div class="col-1">
            <label class="sr-only" for="inlineFormInput">Fat (%)</label>
            <input type="text" class="form-control mb-2" id="fat" value="{{ weight.fat }}" placeholder="Fat (%)">
          </div>
          <div class="col-1">
            <label class="sr-only" for="inlineFormInput">Water (%)</label>
            <input type="text" class="form-control mb-2" id="water" value="{{ weight.water }}" placeholder="Water (%)">
          </div>
          <div class="col-1">
            <label class="sr-only" for="inlineFormInput">Muscle (%)</label>
            <input type="text" class="form-control mb-2" id="muscle" value="{{ weight.muscle }}" placeholder="Muscle (%)">
          </div>
          <div class="col-2">
            <button type="submit" class="btn btn-success">Update</button>
            <button type="submit" class="btn btn-danger">Delete</button>
          </div>
        </div>
      </form>`,
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
