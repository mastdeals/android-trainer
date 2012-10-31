package com.glm.chart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.chart.axis.DateAxis;
import org.afree.chart.plot.XYPlot;
import org.afree.chart.renderer.xy.XYItemRenderer;
import org.afree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.afree.data.category.DefaultCategoryDataset;
import org.afree.data.time.RegularTimePeriod;
import org.afree.data.time.TimeSeries;
import org.afree.data.time.TimeSeriesCollection;
import org.afree.data.xy.DefaultXYDataset;
import org.afree.data.xy.XYDataset;
import org.afree.data.xy.XYSeries;
import org.afree.graphics.SolidColor;
import org.afree.ui.RectangleInsets;

import com.glm.bean.Exercise;
import com.glm.bean.ExerciseManipulate;
import com.glm.bean.WatchPoint;
import com.glm.trainer.R;
import com.glm.utils.ExerciseUtils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

public class LineChart extends Chart{
	
	private static Context oContext;
	
	/**
	 * Crea un grafico a linea di tipo Time Series
	 * param int Type=-1 Weight
	 * 			 Type=0 ALT
	 * 			 Type=1 PACE
	 *           Type=2 BPM
	 * */
	public LineChart(Context context,int Type) {
		super(context);
		oContext=context;
		
		
		switch (Type) {
		//Grafico Weight
		case -1:
			Log.i(this.getClass().getCanonicalName(), "Crete Graph Weight");
			TimeSeriesCollection oDataSetWeight = new TimeSeriesCollection();		
			final AFreeChart chartWeight = createWeightChart(setWeightCategory(oDataSetWeight));
			setChart(chartWeight);
			break;
		case 0:
			Log.i(this.getClass().getCanonicalName(), "Crete Graph ALT");
			XYDataset oDataSetALT = new DefaultXYDataset();	
			final AFreeChart chartALT = createALTChart(setALTCategory(oDataSetALT));
			setChart(chartALT);
			break;
		case 1:
			Log.i(this.getClass().getCanonicalName(), "Crete Graph Pace");
			TimeSeriesCollection oDataSetPace = new TimeSeriesCollection();
			final AFreeChart chartPace = createPaceChart(setPaceCategory(oDataSetPace));
			setChart(chartPace);
			break;	
		case 2:
			Log.i(this.getClass().getCanonicalName(), "Crete Graph BPM");
			TimeSeriesCollection oDataSetBPM = new TimeSeriesCollection();
			final AFreeChart chartBPM = createBPMChart(setBPMCategory(oDataSetBPM));
			setChart(chartBPM);
			break;	
		default:
			break;
		}
		

       
	}
	
	/**Creo il dataset per il grafico*/
	private XYDataset setWeightCategory(TimeSeriesCollection oDataSet){	
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
	
	/**Creo il dataset per il grafico*/
	private XYDataset setBPMCategory(XYDataset oDataSet) {
		XYSeries oSerie = new XYSeries(oContext.getString(R.string.alt));		
		ArrayList<WatchPoint> aWatchPoint = ExerciseManipulate.getWatchPoint();
		double dMin=0.0;
		if(aWatchPoint.size()==0) return null;
		int iWPSize=aWatchPoint.size()-2;
		for(int i=0;i<iWPSize;i++){
			if(dMin==0.0){
				dMin=aWatchPoint.get(i).getBpm();	
			}else if (dMin>aWatchPoint.get(i).getBpm()){
				dMin=aWatchPoint.get(i).getBpm();
			}				
			oSerie.add(Math.round(aWatchPoint.get(i).getdDistance()),aWatchPoint.get(i).getBpm());
		}
		oSerie.add(Math.round(aWatchPoint.get(aWatchPoint.size()-1).getdDistance()),
				aWatchPoint.get(aWatchPoint.size()-1).getBpm());
		
		return oDataSet;
	}

	/**Creo il dataset per il grafico*/
	private XYDataset setPaceCategory(XYDataset oDataSet) {
		XYSeries oSerie = new XYSeries(oContext.getString(R.string.alt));	
		ArrayList<WatchPoint> aWatchPoint = ExerciseManipulate.getWatchPoint();

		if(aWatchPoint.size()==0) return null;
		int iWPSize=aWatchPoint.size()-2;
		for(int i=0;i<iWPSize;i++){
			
			oSerie.add(Math.round(aWatchPoint.get(i).getdDistance()),aWatchPoint.get(i).getdPace());
		}
		oSerie.add(Math.round(aWatchPoint.get(aWatchPoint.size()-1).getdDistance()),
				aWatchPoint.get(aWatchPoint.size()-1).getdPace());
		return oDataSet;
	}

	private XYDataset setALTCategory(XYDataset oDataSet) {
		XYSeries oSerie = new XYSeries(oContext.getString(R.string.alt));		
		ArrayList<WatchPoint> aWatchPoint = ExerciseManipulate.getWatchPoint();
		double dMin=0.0;
		if(aWatchPoint.size()==0) return null;
		int iWPSize=aWatchPoint.size()-2;
		for(int i=0;i<iWPSize;i++){
			if(dMin==0.0){
				dMin=aWatchPoint.get(i).getdAlt();	
			}else if (dMin>aWatchPoint.get(i).getdAlt()){
				dMin=aWatchPoint.get(i).getdAlt();
			}				
			oSerie.add(Math.round(aWatchPoint.get(i).getdDistance()),aWatchPoint.get(i).getdAlt());
		}
		oSerie.add(Math.round(aWatchPoint.get(aWatchPoint.size()-1).getdDistance()),
				aWatchPoint.get(aWatchPoint.size()-1).getdAlt());
		
		return oDataSet;
	}

	/**
     * Creates a chart.
     *
     * @param dataset  a dataset.
     *
     * @return A chart.
     */
    private static AFreeChart createWeightChart(XYDataset dataset) {

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
	
    /**
     * Creates a chart.
     *
     * @param dataset  a dataset.
     *
     * @return A chart.
     */
    private static AFreeChart createALTChart(XYDataset dataset) {

        AFreeChart chart = ChartFactory.createTimeSeriesChart(
        		oContext.getString(R.string.alt),  // title
        		oContext.getString(R.string.time),    // x-axis label
        		oContext.getString(R.string.alt),   // y-axis label
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
       
        return chart;

    }
    
    /**
     * Creates a chart.
     *
     * @param dataset  a dataset.
     *
     * @return A chart.
     */
    private static AFreeChart createPaceChart(XYDataset dataset) {

        AFreeChart chart = ChartFactory.createTimeSeriesChart(
        		oContext.getString(R.string.pace),  // title
        		oContext.getString(R.string.time),    // x-axis label
        		oContext.getString(R.string.pace),   // y-axis label
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
        
        return chart;

    }
    
    /**
     * Creates a chart.
     *
     * @param dataset  a dataset.
     *
     * @return A chart.
     */
    private static AFreeChart createBPMChart(XYDataset dataset) {

        AFreeChart chart = ChartFactory.createTimeSeriesChart(
        		oContext.getString(R.string.heart_rate),  // title
        		oContext.getString(R.string.time),    // x-axis label
        		oContext.getString(R.string.heart_rate),   // y-axis label
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
      
        return chart;

    }	
}
