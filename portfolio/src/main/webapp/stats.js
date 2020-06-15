
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/** Fetches color data and uses it to create a chart. */
function drawChart() {
  fetch('/stats', {method: 'POST'}).then(response => response.json())
  .then((stats) => {
    console.log(stats);
    const data = new google.visualization.DataTable();
    const wordCount = JSON.parse(stats.wCount);
    console.log(wordCount);
    data.addColumn('string', 'word');
    data.addColumn('number', 'count');
    Object.keys(wordCount).forEach((word) => {
      data.addRow([word, wordCount[word]]);
    });

    const options = {
      'title': 'Word  Count',
      'width':1000,
      'height':700
    };

    const chart = new google.visualization.ColumnChart(
        document.getElementById('chart-container'));
    chart.draw(data, options);
  });
}