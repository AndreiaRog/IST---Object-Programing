package mmt;
import mmt.Visitable;
import mmt.Service;


import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.time.LocalTime;

/**
 * This class represents the product that the TrainCompany offers to its customers
 * (Passengers). The  services are used to build the itineraries. Each itinerary
 * has one or more services. Each passenger has 0 or more itineraries associated.
 */
public class Segment extends Service{
	//cost será o custo do serviço associado


	//baseCost será o custo do serviço/tempo de viagem
	private double _baseCost;

	private double _segmentCost = 0.0;

    public Segment(int id, double cost, long serviceTimeMin) {
        super(id, cost);
        _baseCost =  cost/serviceTimeMin;
        /*_segmentCost = calcSegmentCost(); */
    }

    public double calcSegmentCost(){
    	long segmentTimeMin = getDuration();
    	double segmentCost= _baseCost*segmentTimeMin;
        _segmentCost = segmentCost;
    	return _segmentCost;
    }

    @Override
    public double getCost(){
        return _segmentCost;
    }


}
