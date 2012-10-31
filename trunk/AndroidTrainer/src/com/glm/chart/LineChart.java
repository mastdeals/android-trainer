package com.glm.chart;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.axis.DateAxis;
import org.afree.chart.plot.XYPlot;
import org.afree.chart.renderer.xy.XYItemRenderer;
import org.afree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.afree.data.time.RegularTimePeriod;
import org.afree.data.time.TimeSeries;
import org.afree.data.time.TimeSeriesCollection;
import org.afree.data.xy.XYDataset;
import org.afree.graphics.SolidColor;
import org.afree.ui.RectangleInsets;

import com.glm.bean.Exercise;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;

import android.content.Context;
import android.graphics.Color;

public class LineChart extends Chart{
	private TimeSeriesCollection oDataSet;
	private static Context oContext;
	
	/**
	 * Crea un grafico a linea di tipo Time Series
	 * param int Type=0 Weight
	 * 			 Type=1 Pace
	 * 			 Type=2 Speed
	 *           Type=3 BPM
	 * */
	public LineChart(Context context,int Type) {
		super(context);
		oContext=context;
		oDataSet = new TimeSeriesCollection();		
		
		switch (Type) {
		//Grafico Weight
		case 0:
			final AFreeChart chart = createChart(setWeightCategory());
			setChart(chart);
			break;

		default:
			break;
		}
		

       
	}
	
	/**
     * Creates a chart.
     *
     * @param dataset  a dataset.
     *
     * @return A chart.
     */
    private static AFreeChart createChart(XYDataset dataset) {

        AFreeChart chart = ChartFactory.createTimeSeriesChart(
        		oContext.getString(R.string.weight),  // title
        		oContext.getString(R.string.time),    // x-axis label
        		oContext.getString(R.string.weight),   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
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
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy"));

        return chart;

    }
	
	
	/**Creo il dataset per il grafico*/
	private XYDataset setWeightCategory(){	
		Vector<Exercise> oExercise=ExerciseUtils.getWeightData(oContext);
		if(oExercise.size()==0) return null;
		int iExSize=oExercise.size()-2;		
		TimeSeries oSerie = new TimeSeries(oContext.getString(R.string.weight));
		
		for(int i=0;i<iExSize;i++){
			oSerie.add(RegularTimePeriod.createInstance(getClass(), new Date(oExercise.get(i).getdDateExercise().getTime()), TimeZone.getDefault()), oExercise.get(i).getdWeight());			
		}
		oSerie.add(RegularTimePeriod.createInstance(getClass(), new Date(oExercise.get(oExercise.size()-1).getdDateExercise().getTime()), TimeZone.getDefault()), oExercise.get(oExercise.size()-1).getdWeight());
		oDataSet.addSeries(oSerie);
		//Log.v(this.getClass().getCanonicalName(), "Data for Jflot: "+sData);
		return  oDataSet;
	}
}
