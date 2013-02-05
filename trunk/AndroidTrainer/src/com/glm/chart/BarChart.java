package com.glm.chart;

import java.text.SimpleDateFormat;
import java.util.Vector;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.axis.DateAxis;
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.plot.XYPlot;
import org.afree.chart.renderer.xy.XYItemRenderer;
import org.afree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.afree.data.time.Month;
import org.afree.data.time.TimeSeries;
import org.afree.data.time.TimeSeriesCollection;
import org.afree.data.xy.IntervalXYDataset;
import org.afree.graphics.SolidColor;
import org.afree.ui.RectangleInsets;

import com.glm.bean.DistancePerMonth;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

public class BarChart extends Chart{
	
	private static Context oContext;
	
	/**
	 * Crea un grafico a linea di tipo Time Series
	 * param int Type=0 Summary
	 *           
	 * */
	public BarChart(Context context,int Type) {
		super(context);
		oContext=context;
		
		
		switch (Type) {		
		case 0:
			Log.i(this.getClass().getCanonicalName(), "Crete Graph Summary");
			
			final AFreeChart chartSummary = createSummaryChart(setSummaryCategory());
			setChart(chartSummary);
			break;		
		default:
			break;
		}
		

       
	}

	private IntervalXYDataset setSummaryCategory() {
		TimeSeriesCollection oDataSet = new TimeSeriesCollection();		
		TimeSeries oSerie = new TimeSeries(oContext.getString(R.string.distance));
		

		Vector<DistancePerMonth> table = ExerciseUtils.getDistanceForMonth( ExerciseUtils.loadConfiguration(oContext,true), 
				oContext);
			
		for(int i=0;i<11;i++){
			DistancePerMonth oDistance = (DistancePerMonth) table.get(i);	
			Number nDistance = 0;
			try{
				nDistance = Integer.parseInt(oDistance.getsDistance());
			}catch(NumberFormatException e){
				Log.e(this.getClass().getCanonicalName(),"Error Parse Number"+oDistance.getsDistance());
			}
			Log.v(this.getClass().getCanonicalName(),"Month: "+oDistance.getiMonth()+" - Distance: "+nDistance);
			oSerie.add(new Month(oDistance.getiMonth(), 2012),nDistance);
			oDistance=null;
			nDistance=null;
		}
		
		Number nDistance = Integer.parseInt(((DistancePerMonth) table.get(table.size()-1)).getsDistance());
		oSerie.add(new Month(((DistancePerMonth) table.get(table.size()-1)).getiMonth(), 2012),
				nDistance);
		
		nDistance=null;
		oDataSet.addSeries(oSerie);
		return oDataSet;
	}

	
	
    /**
     * Creates a chart.
     *
     * @param dataset  a dataset.
     *
     * @return A chart.
     */
    private static AFreeChart createSummaryChart(IntervalXYDataset dataset) {
    	//ChartFactory.createXYBarChart(title, xAxisLabel, dateAxis, yAxisLabel, dataset, orientation, legend, tooltips, urls)
        AFreeChart chart = ChartFactory.createXYBarChart(
        		null,  // title
        		null,    // x-axis label
        		true,
        		null,   // y-axis label
            dataset,            // data
            PlotOrientation.VERTICAL,
            false,               // create legend?
            false,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaintType(new SolidColor(Color.WHITE));

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaintType(new SolidColor(Color.LTGRAY));
        plot.setDomainGridlinePaintType(new SolidColor(Color.WHITE));
        plot.setRangeGridlinePaintType(new SolidColor(Color.WHITE));
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(true);
            renderer.setBaseShapesFilled(true);
            renderer.setDrawSeriesLineAsPath(true);
        }
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM"));       
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        return chart;

    }
}
