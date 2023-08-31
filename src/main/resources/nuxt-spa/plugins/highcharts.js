import Vue from "vue";
import Highcharts from "highcharts";
import highchartsMore from "highcharts/highcharts-more";
import networkGraph from "highcharts/modules/networkgraph";
import drag from "highcharts/modules/draggable-points";
import venn from "highcharts/modules/venn";
import gauge from "highcharts/modules/solid-gauge";
import HighchartsVue from "highcharts-vue";
import funnel from 'highcharts/modules/funnel'
import accessibility from "highcharts/modules/accessibility";  // eslint-disable-line
import exporting from "highcharts/modules/exporting";
import offlineExporting from "highcharts/modules/offline-exporting";
import exportData from "highcharts/modules/export-data";

if (typeof Highcharts === "object") {
  highchartsMore(Highcharts);
  networkGraph(Highcharts);
  exporting(Highcharts);
  offlineExporting(Highcharts);
  exportData(Highcharts);
  accessibility(Highcharts);
  drag(Highcharts);
  venn(Highcharts);
  gauge(Highcharts);
  funnel(Highcharts);
  (function (H) {
    H.wrap(
      H.seriesTypes.packedbubble.prototype,
      "onMouseDown",
      function (proceed, point, event) {
        proceed.apply(this, Array.prototype.slice.call(arguments, 1));
        point.importEvents();
        H.fireEvent(point, "dragStart", event);
      }
    );
    H.wrap(
      H.seriesTypes.packedbubble.prototype,
      "onMouseMove",
      function (proceed, point, event) {
        proceed.apply(this, Array.prototype.slice.call(arguments, 1));
        if (point.fixedPosition && point.inDragMode) {
          H.fireEvent(point, "drag", event);
        }
      }
    );
    H.wrap(
      H.seriesTypes.packedbubble.prototype,
      "onMouseUp",
      function (proceed, point, event) {
        if (point.fixedPosition && point.inDragMode) {
          H.fireEvent(point, "drop", point);
        }
        proceed.apply(this, Array.prototype.slice.call(arguments, 1));
      }
    );
  })(Highcharts);
}
Vue.use(HighchartsVue);
