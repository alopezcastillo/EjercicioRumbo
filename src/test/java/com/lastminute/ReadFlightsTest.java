package com.lastminute;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import static com.lastminute.utils.CsvFiles.readAllRecords;
import static com.lastminute.utils.CsvFiles.fullPathTo;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ReadFlightsTest
{
  private final int flightCount = 89;

  @Test
  public void readFlightPrices()
  {
    List<List<String>> prices = readAllRecords(fullPathTo("flight-prices.csv"));

    allFlightsRead(prices);
    startsWith(prices, asList("IB2818", "186"));
    endsWith(prices, asList("LH7260", "191"));
  }

  @Test
  public void readFlightRoutes()
  {
    List<List<String>> routes = readAllRecords(fullPathTo("flight-routes.csv"));

    allFlightsRead(routes);
    startsWith(routes, asList("CPH", "FRA", "IB2818"));
    endsWith(routes, asList("MAD", "AMS", "LH7260"));
  }

  @Test
  public void obtenerRutasOkTest()
  {
	  List<List<String>> routes = readAllRecords(fullPathTo("flight-routes.csv"));
	  
	  List<String> rutas= FlightSearch.obtenerRutas("BCN", "MAD", routes);
	  
	  assertEquals(rutas.size(),2);
	  for(String cod:rutas)
	  {
		  assertEquals(cod.length(), 6);
	  }
	  
  }
  @Test
  public void obtenerRutasNotFoundTest()
  {
	  List<List<String>> routes = readAllRecords(fullPathTo("flight-routes.csv"));
	  
	  //Aeoripuerto nulo
	  List<String> rutas= FlightSearch.obtenerRutas(null, "MAD", routes);
	  assertEquals(rutas.size(),0);
	  //Aeropuerto no encontrado
	  rutas= FlightSearch.obtenerRutas("DESCONOCIDO", "MAD", routes);
	  assertEquals(rutas.size(),0);
	  
	  //Sin rutas
	  rutas= FlightSearch.obtenerRutas("BCN", "MAD", new ArrayList<List<String>>());
	  assertEquals(rutas.size(),0);
	  
  }
  @Test
  public void presupuestoVueloUnitarioOkTest()
  {
	  List<String> vuelos=new ArrayList<String>();
	  vuelos.add("FR7521");
	  List<List<String>> precios = readAllRecords(fullPathTo("flight-prices.csv"));
	  LocalDate fecha = LocalDate.now( );
	  Date date = Date.from((fecha.plusDays(2)).atStartOfDay(ZoneId.systemDefault()).toInstant());
	  List<List<Object>> presupuesto= FlightSearch.presupuestoVueloUnitario(vuelos, precios, date);
	  double precioBase=(Double)presupuesto.get(0).get(1);
	  //menos de 3 días
	  assertEquals(precioBase, 150*1.5,0.000001);
	  //3 a 15
	  date = Date.from((fecha.plusDays(3)).atStartOfDay(ZoneId.systemDefault()).toInstant());
	  presupuesto= FlightSearch.presupuestoVueloUnitario(vuelos, precios, date);
	  precioBase=(Double)presupuesto.get(0).get(1);
	  assertEquals(precioBase, 150*1.2,0.000001);
	  //16 a 30
	  date = Date.from((fecha.plusDays(16)).atStartOfDay(ZoneId.systemDefault()).toInstant());
	  presupuesto= FlightSearch.presupuestoVueloUnitario(vuelos, precios, date);
	  precioBase=(Double)presupuesto.get(0).get(1);
	  assertEquals(precioBase, 150,0.000001);
	  //más de 30
	  date = Date.from((fecha.plusDays(31)).atStartOfDay(ZoneId.systemDefault()).toInstant());
	  presupuesto= FlightSearch.presupuestoVueloUnitario(vuelos, precios, date);
	  precioBase=(Double)presupuesto.get(0).get(1);
	  assertEquals(precioBase, 150*0.8,0.000001);
	  
  }
  @Test(expected=IllegalArgumentException.class)
  public void presupuestoVueloUnitarioErrorsTest()
  {
	  List<String> vuelos=new ArrayList<String>();
	  vuelos.add("NO_ENCONTRADO");
	  List<List<String>> precios = readAllRecords(fullPathTo("flight-prices.csv"));
	  LocalDate fecha = LocalDate.now( );
	  Date date = Date.from((fecha.plusDays(2)).atStartOfDay(ZoneId.systemDefault()).toInstant());
	  List<List<Object>> presupuesto= FlightSearch.presupuestoVueloUnitario(vuelos, precios, date);
	  
	  //no encontrado
	  assertEquals(presupuesto.isEmpty(), true);
	  //fecha no válida
	  vuelos.clear();
	  vuelos.add("FR7521");
	  date = Date.from((fecha.minusDays(2)).atStartOfDay(ZoneId.systemDefault()).toInstant());
	  FlightSearch.presupuestoVueloUnitario(vuelos, precios, date);
	  
	  
  }
  

  private void allFlightsRead(List<List<String>> flights)
  {
    assertEquals(flightCount, flights.size());
  }

  private void startsWith(List<List<String>> actual, List<String> expected)
  {
    assertEquals(expected, actual.get(0));
  }

  private void endsWith(List<List<String>> actual, List<String> expected)
  {
    assertEquals(expected, actual.get(actual.size() - 1));
  }
}
