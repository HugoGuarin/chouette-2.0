/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.xml.neptune.importer;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.ITL;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.AccessLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.AccessPointProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.AreaCentroidProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.CompanyProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.ConnectionLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.FacilityProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.GroupOfLineProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.JourneyPatternProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.LineProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.PTLinkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.RouteProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.RoutingConstraintProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.StopAreaProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.StopPointProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.TimeSlotProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.TimetableProducer;
import fr.certu.chouette.exchange.xml.neptune.importer.producer.VehicleJourneyProducer;
import fr.certu.chouette.exchange.xml.neptune.model.NeptuneRoutingConstraint;
import fr.certu.chouette.exchange.xml.neptune.report.NeptuneReportItem;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourneyFactory;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * convert each Castor Neptune item in corresponding Chouette Neptune one
 * 
 */
public class NeptuneConverter
{

   /**
    * line producer
    */
   @Getter
   @Setter
   private LineProducer              lineProducer;
   /**
    * route producer
    */
   @Getter
   @Setter
   private RouteProducer             routeProducer;
   /**
    * network producer
    */
   @Getter
   @Setter
   private PTNetworkProducer         networkProducer;
   /**
    * company producer
    */
   @Getter
   @Setter
   private CompanyProducer           companyProducer;
   /**
    * journey pattern producer
    */
   @Getter
   @Setter
   private JourneyPatternProducer    journeyPatternProducer;
   /**
    * ptLink producer
    */
   @Getter
   @Setter
   private PTLinkProducer            ptLinkProducer;
   /**
    * vehicle journey and vehicle journay at stop producer
    */
   @Getter
   @Setter
   private VehicleJourneyProducer    vehicleJourneyProducer;
   /**
    * stop point producer
    */
   @Getter
   @Setter
   private StopPointProducer         stopPointProducer;
   /**
    * stop area producer
    */
   @Getter
   @Setter
   private StopAreaProducer          stopAreaProducer;
   /**
    * area centriod producer
    */
   @Getter
   @Setter
   private AreaCentroidProducer      areaCentroidProducer;
   /**
    * connection link producer
    */
   @Getter
   @Setter
   private ConnectionLinkProducer    connectionLinkProducer;
   /**
    * time table producer
    */
   @Getter
   @Setter
   private TimetableProducer         timetableProducer;
   /**
    * routing contraint producer
    */
   @Getter
   @Setter
   private RoutingConstraintProducer routingConstraintProducer;
   /**
    * access link producer
    */
   @Getter
   @Setter
   private AccessLinkProducer        accessLinkProducer;
   /**
    * access point producer
    */
   @Getter
   @Setter
   private AccessPointProducer       accessPointProducer;
   /**
    * group of line producer
    */
   @Getter
   @Setter
   private GroupOfLineProducer       groupOfLineProducer;
   /**
    * facility producer
    */
   @Getter
   @Setter
   private FacilityProducer          facilityProducer;
   /**
    * time slot producer
    */
   @Getter
   @Setter
   private TimeSlotProducer          timeSlotProducer;

   /**
    * extract the line
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return line produced from ChouetteLineDescription
    */
   public Line extractLine(ChouettePTNetworkTypeType rootObject, ReportItem parentReport)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "Line");
      ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
      chouette.schema.Line xmlLine = lineDescription.getLine();

      Line line = lineProducer.produce(xmlLine, report,null);

      int count = (line == null ? 0 : 1);
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return line;
   }

   /**
    * extract routes
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return routes produced from ChouetteLineDescription.ChouetteRoute
    */
   public List<Route> extractRoutes(ChouettePTNetworkTypeType rootObject, ReportItem parentReport)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "ChouetteRoute");
      ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
      chouette.schema.ChouetteRoute[] xmlRoutes = lineDescription.getChouetteRoute();

      List<Route> routes = new ArrayList<Route>();

      for (chouette.schema.ChouetteRoute xmlRoute : xmlRoutes)
      {
         Route route = routeProducer.produce(xmlRoute, report,null);
         routes.add(route);
      }

      int count = (routes == null ? 0 : routes.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return routes;
   }

   /**
    * extract routing constraint relations
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return NeptuneRoutingConstraint produced from ChouetteLineDescription.ITL
    */
   public List<NeptuneRoutingConstraint> extractRoutingConstraints(ChouettePTNetworkTypeType rootObject,
         ReportItem parentReport)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "ITL");
      ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
      ITL[] itls = lineDescription.getITL();

      List<NeptuneRoutingConstraint> restrictionConstraints = routingConstraintProducer.produce(itls, report);

      int count = (restrictionConstraints == null ? 0 : restrictionConstraints.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return restrictionConstraints;
   }

   /**
    * extract companies
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return companies produced from Company
    */
   public List<Company> extractCompanies(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,SharedImportedData sharedData)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "Company");
      chouette.schema.Company[] xmlCompanies = rootObject.getCompany();

      List<Company> companies = new ArrayList<Company>();

      for (chouette.schema.Company xmlCompany : xmlCompanies)
      {
         Company company = companyProducer.produce(xmlCompany, report,sharedData);
         companies.add(company);
      }

      int count = (companies == null ? 0 : companies.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return companies;
   }

   /**
    * extract network
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return network produced from PTNetwork
    */
   public PTNetwork extractPTNetwork(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,SharedImportedData sharedData)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "PTNetwork");
      chouette.schema.PTNetwork xmlPTNetwork = rootObject.getPTNetwork();

      PTNetwork ptNetwork = networkProducer.produce(xmlPTNetwork, report,sharedData);

      int count = (ptNetwork == null ? 0 : 1);
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return ptNetwork;
   }

   /**
    * extract journey patterns
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return journeyPatterns produced from
    *         ChouetteLineDescription.JourneyPattern
    */
   public List<JourneyPattern> extractJourneyPatterns(ChouettePTNetworkTypeType rootObject, ReportItem parentReport)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "JourneyPattern");
      ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
      chouette.schema.JourneyPattern[] xmlJourneyPatterns = lineDescription.getJourneyPattern();

      List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();

      for (chouette.schema.JourneyPattern xmlJourneyPattern : xmlJourneyPatterns)
      {
         JourneyPattern journeyPattern = journeyPatternProducer.produce(xmlJourneyPattern, report,null);
         journeyPatterns.add(journeyPattern);
      }

      int count = (journeyPatterns == null ? 0 : journeyPatterns.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return journeyPatterns;
   }

   /**
    * extract PTLinks
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return PTLinks produced from ChouetteLineDescription.PtLink
    */
   public List<PTLink> extractPTLinks(ChouettePTNetworkTypeType rootObject, ReportItem parentReport)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "PtLink");
      ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
      chouette.schema.PtLink[] xmlPTLinks = lineDescription.getPtLink();

      List<PTLink> ptLinks = new ArrayList<PTLink>();

      for (chouette.schema.PtLink xmlPTLink : xmlPTLinks)
      {
         PTLink ptLink = ptLinkProducer.produce(xmlPTLink, report,null);
         ptLinks.add(ptLink);
      }

      int count = (ptLinks == null ? 0 : ptLinks.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return ptLinks;
   }

   /**
    * extract VehicleJourneys
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return VehicleJourneys produced from
    *         ChouetteLineDescription.VehicleJourney
    */
   public List<VehicleJourney> extractVehicleJourneys(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,boolean optimizeMemory)
   {
      DbVehicleJourneyFactory vjFactory = new DbVehicleJourneyFactory("Neptune",optimizeMemory);
      vehicleJourneyProducer.setFactory(vjFactory);
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "VehicleJourney");
      ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
      chouette.schema.VehicleJourney[] xmlVehicleJourneys = lineDescription.getVehicleJourney();

      List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();

      for (chouette.schema.VehicleJourney xmlVehicleJourney : xmlVehicleJourneys)
      {
         VehicleJourney vehicleJourney = vehicleJourneyProducer.produce(xmlVehicleJourney, report, null);
         vehicleJourneys.add(vehicleJourney);
      }

      int count = (vehicleJourneys == null ? 0 : vehicleJourneys.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return vehicleJourneys;
   }

   /**
    * extract StopPoints
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return StopPoints produced from ChouetteLineDescription.StopPoint
    */
   public List<StopPoint> extractStopPoints(ChouettePTNetworkTypeType rootObject, ReportItem parentReport)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "StopPoint");
      ChouetteLineDescription lineDescription = rootObject.getChouetteLineDescription();
      chouette.schema.StopPoint[] xmlStopPoints = lineDescription.getStopPoint();

      List<StopPoint> stopPoints = new ArrayList<StopPoint>();

      for (chouette.schema.StopPoint xmlStopPoint : xmlStopPoints)
      {
         StopPoint stopPoint = stopPointProducer.produce(xmlStopPoint, report,null);
         stopPoints.add(stopPoint);
      }

      int count = (stopPoints == null ? 0 : stopPoints.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return stopPoints;
   }

   /**
    * extract StopAreas
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return StopAreas produced from ChouetteArea.StopArea
    */
   public List<StopArea> extractStopAreas(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,SharedImportedData sharedData)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "StopArea");
      chouette.schema.StopArea[] xmlStopAreas = rootObject.getChouetteArea().getStopArea();

      List<StopArea> stopAreas = new ArrayList<StopArea>();

      for (chouette.schema.StopArea xmlStopArea : xmlStopAreas)
      {
         StopArea stopArea = stopAreaProducer.produce(xmlStopArea, report,sharedData);
         stopAreas.add(stopArea);
      }

      int count = (stopAreas == null ? 0 : stopAreas.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return stopAreas;
   }

   /**
    * extract AreaCentroids
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return AreaCentroids produced from ChouetteArea.AreaCentroid
    */
   public List<AreaCentroid> extractAreaCentroids(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,SharedImportedData sharedData)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "AreaCentroid");
      chouette.schema.AreaCentroid[] xmlAreaCentroids = rootObject.getChouetteArea().getAreaCentroid();

      List<AreaCentroid> areaCentroids = new ArrayList<AreaCentroid>();

      for (chouette.schema.AreaCentroid xmlAreaCentroid : xmlAreaCentroids)
      {
         AreaCentroid areaCentroid = areaCentroidProducer.produce(xmlAreaCentroid, report,sharedData);
         areaCentroids.add(areaCentroid);
      }

      int count = (areaCentroids == null ? 0 : areaCentroids.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return areaCentroids;
   }

   /**
    * extract ConnectionLinks
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return ConnectionLinks produced from ConnectionLink
    */
   public List<ConnectionLink> extractConnectionLinks(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,SharedImportedData sharedData)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "ConnectionLink");
      chouette.schema.ConnectionLink[] xmlConnectionLinks = rootObject.getConnectionLink();

      List<ConnectionLink> connectionLinks = new ArrayList<ConnectionLink>();

      for (chouette.schema.ConnectionLink xmlConnectionLink : xmlConnectionLinks)
      {
         ConnectionLink connectionLink = connectionLinkProducer.produce(xmlConnectionLink, report,sharedData);
         connectionLinks.add(connectionLink);
      }

      int count = (connectionLinks == null ? 0 : connectionLinks.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return connectionLinks;
   }

   /**
    * extract Timetables
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return Timetables produced from Timetable
    */
   public List<Timetable> extractTimetables(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,SharedImportedData sharedData)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "Timetable");
      chouette.schema.Timetable[] xmlTimetables = rootObject.getTimetable();

      List<Timetable> timetables = new ArrayList<Timetable>();

      for (chouette.schema.Timetable xmlTimetable : xmlTimetables)
      {
         Timetable timetable = timetableProducer.produce(xmlTimetable, report,sharedData);
         timetables.add(timetable);
      }

      int count = (timetables == null ? 0 : timetables.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return timetables;
   }

   /**
    * extract AccessLinks
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return AccessLinks produced from AccessLink
    */
   public List<AccessLink> extractAccessLinks(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,SharedImportedData sharedData)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "AccessLink");
      chouette.schema.AccessLink[] xmlAccessLinks = rootObject.getAccessLink();

      List<AccessLink> accessLinks = new ArrayList<AccessLink>();

      for (chouette.schema.AccessLink xmlAccessLink : xmlAccessLinks)
      {
         AccessLink accessLink = accessLinkProducer.produce(xmlAccessLink, report,sharedData);
         accessLinks.add(accessLink);
      }

      int count = (accessLinks == null ? 0 : accessLinks.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return accessLinks;
   }

   /**
    * extract AccessPoints
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return AccessPoints produced from AccessPoint
    */
   public List<AccessPoint> extractAccessPoints(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,SharedImportedData sharedData)
   {
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "AccessLink");
      chouette.schema.AccessPoint[] xmlAccessPoints = rootObject.getAccessPoint();

      List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();

      for (chouette.schema.AccessPoint xmlAccessPoint : xmlAccessPoints)
      {
         AccessPoint accessPoint = accessPointProducer.produce(xmlAccessPoint, report,sharedData);
         accessPoints.add(accessPoint);
      }

      int count = (accessPoints == null ? 0 : accessPoints.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return accessPoints;
   }

   /**
    * extract GroupOfLines
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return GroupOfLines produced from GroupOfLine
    */
   public List<GroupOfLine> extractGroupOfLines(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,SharedImportedData sharedData)
   {
      List<GroupOfLine> groupOfLines = new ArrayList<GroupOfLine>();

      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "GroupOfLine");
      chouette.schema.GroupOfLine[] xmlGroupOfLines = rootObject.getGroupOfLine();
      for (chouette.schema.GroupOfLine xmlGroupOfLine : xmlGroupOfLines)
      {
         GroupOfLine groupOfLine = groupOfLineProducer.produce(xmlGroupOfLine, report,sharedData);
         groupOfLines.add(groupOfLine);
      }

      
      int count = (groupOfLines == null ? 0 : groupOfLines.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return groupOfLines;
   }

   /**
    * extract Facilities
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return Facilities produced from Facility
    */
   public List<Facility> extractFacilities(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,SharedImportedData sharedData)
   {
      List<Facility> facilities = new ArrayList<Facility>();
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "Facility");
      chouette.schema.Facility[] xmlFacilities = rootObject.getFacility();
      for (chouette.schema.Facility xmlFacility : xmlFacilities)
      {
         Facility facility = facilityProducer.produce(xmlFacility, report, sharedData);
         facilities.add(facility);
      }
      int count = (facilities == null ? 0 : facilities.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);
      return facilities;
   }

   /**
    * extract TimeSlots
    * 
    * @param rootObject
    *           Castor root XML object
    * @param parentReport
    *           error report
    * @return TimeSlots produced from TimeSlot
    */
   public List<TimeSlot> extractTimeSlots(ChouettePTNetworkTypeType rootObject, ReportItem parentReport,SharedImportedData sharedData)
   {
      List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();
      ReportItem report = new NeptuneReportItem(NeptuneReportItem.KEY.PARSE_OBJECT, Report.STATE.OK, "TimeSlot");
      chouette.schema.TimeSlot[] xmlTimeSlots = rootObject.getTimeSlot();
      for (chouette.schema.TimeSlot xmlTimeSlot : xmlTimeSlots)
      {
         TimeSlot timeSlot = timeSlotProducer.produce(xmlTimeSlot, report,sharedData);
         timeSlots.add(timeSlot);
      }
      int count = (timeSlots == null ? 0 : timeSlots.size());
      ReportItem countItem = new NeptuneReportItem(NeptuneReportItem.KEY.OBJECT_COUNT, Report.STATE.OK,
            Integer.toString(count));
      report.addItem(countItem);
      parentReport.addItem(report);

      return timeSlots;
   }
}
