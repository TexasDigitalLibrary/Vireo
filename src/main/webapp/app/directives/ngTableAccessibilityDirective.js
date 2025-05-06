angular.module('vireo').directive('ngTableAccessibility', function() {
  return {
    restrict: 'A',
    link: function(scope, element) {
      console.log('ngTableAccessibility directive initialized');

      // Function to process table headers
      function processHeaders() {
        // Process main headers
        var headers = element[0].querySelectorAll('thead th:not(.filter)');
        if (headers && headers.length) {
          console.log('Processing ' + headers.length + ' table headers');
          headers.forEach(function(header) {
            if (!header.getAttribute('role')) {
              header.setAttribute('role', 'columnheader');
              header.setAttribute('scope', 'col');

              var titleText = header.textContent.trim();
              if (titleText && !header.querySelector('.sr-only')) {
                var label = document.createElement('span');
                label.className = 'sr-only';
                label.textContent = titleText + ' column';
                header.insertBefore(label, header.firstChild);
              }
            }
          });
        }

        // Process filter headers
        var filterHeaders = element[0].querySelectorAll('tr.ng-table-filters th');
        if (filterHeaders && filterHeaders.length) {
          console.log('Processing ' + filterHeaders.length + ' filter headers');
          filterHeaders.forEach(function(header) {
            // Get the column title from data-title-text attribute
            var titleText = header.getAttribute('data-title-text');
            if (titleText && !header.querySelector('.sr-only')) {
              header.setAttribute('role', 'columnheader');
              header.setAttribute('scope', 'col');

              // Add descriptive text for screen readers
              var label = document.createElement('span');
              label.className = 'sr-only';
              label.textContent = titleText + ' filter';

              // Insert at the beginning of the header
              header.insertBefore(label, header.firstChild);
            }
          });
        }

        // Ensure table has proper role
        element[0].setAttribute('role', 'grid');
        var tbody = element[0].querySelector('tbody');
        if (tbody) {
          tbody.setAttribute('role', 'rowgroup');
        }
      }

      // Function to process table cells
      function processCells() {
        var rows = element[0].querySelectorAll('tbody tr');
        if (rows && rows.length) {
          console.log('Processing ' + rows.length + ' table rows');
          rows.forEach(function(row, rowIndex) {
            row.setAttribute('role', 'row');

            var cells = row.querySelectorAll('td');
            cells.forEach(function(cell, cellIndex) {
              cell.setAttribute('role', 'gridcell');

              var titleText = cell.getAttribute('title') ||
                             element[0].querySelector('thead th:not(.filter):nth-child(' + (cellIndex + 1) + ')')?.textContent.trim();

              var cellContent = cell.textContent || '';
              var hasButtons = cell.querySelector('button, a');
              var hasVisibleContent = cellContent.trim() &&
                                    !['Not Submitted', 'Not Assigned', 'No Title', 'No Primary Document']
                                    .includes(cellContent.trim());

              // Handle empty or placeholder content cells
              if (!hasButtons && !hasVisibleContent && titleText) {
                var existingLabel = cell.querySelector('.sr-only');
                if (!existingLabel) {
                  var label = document.createElement('span');
                  label.className = 'sr-only';
                  label.textContent = 'No ' + titleText.toLowerCase() + ' available';
                  cell.appendChild(label);
                }
              }

              // Handle action buttons
              if (hasButtons) {
                var buttons = cell.querySelectorAll('button, a');
                buttons.forEach(function(button) {
                  if (!button.getAttribute('aria-label')) {
                    var buttonText = button.textContent.trim();
                    var rowContext = cells[0]?.textContent.trim() || 'item';
                    button.setAttribute('aria-label', buttonText + ' ' + rowContext);
                  }
                });
              }
            });
          });
        }
      }

      // Watch for changes in the table structure
      var observer = new MutationObserver(function(mutations) {
        processHeaders();
        processCells();
      });

      // Start observing the table for dynamic changes
      observer.observe(element[0], {
        childList: true,
        subtree: true,
        attributes: true,
        attributeFilter: ['class', 'style'] // Watch for class/style changes that might show/hide filters
      });

      // Initial processing
      processHeaders();
      processCells();

      // Clean up observer when scope is destroyed
      scope.$on('$destroy', function() {
        observer.disconnect();
      });
    }
  };
});

