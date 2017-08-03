package com.lastminute;

import static com.lastminute.utils.CsvFiles.readAllRecords;
import static com.lastminute.utils.CsvFiles.fullPathTo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FlightSearch {
	
	
	//Multiplicadores de precio según la antelación
	private static final double PRECIO_MEN_3 = 1.5;
	private static final double PRECIO_MAY_2 = 1.2;
	private static final int PRECIO_MAY_15 = 1;
	private static final double PRECIO_MAY_30 = 0.8;





	public static void main(String[] args)
	{
		
		List<List<String>> rutas= readAllRecords(fullPathTo("flight-routes.csv"));
		List<List<String>> precios= readAllRecords(fullPathTo("flight-prices.csv"));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		boolean continuar=true;
		int pasajeros=0;
		String origen,destino,fechaStr;
		Date fecha;
		// Date flightDate 
		
		while(continuar)
		{
			
		try {
			System.out.println("\nIntroduce Aeropuerto origen\n***************************");
			origen =  br.readLine().toUpperCase();
			System.out.println("Introduce Aeropuerto destino");
			destino =  br.readLine().toUpperCase();
			System.out.println("Introduce fecha de vuelo formato yyyyy-mm-dd");
			fechaStr= br.readLine();
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyyy-MM-dd");
			fecha = dt1.parse(fechaStr)  ;
			System.out.println("Introduce cantidad de pasajeros");
			pasajeros= Integer.parseInt( br.readLine());
			
			List<String> vuelos = obtenerRutas( origen, destino,rutas);
			if(vuelos.isEmpty())
			{System.out.println("No se ha encontrado ningún vuelo con esa ruta");}
			else
			{
				List<List<Object>> presupuestos= presupuestoVueloUnitario(vuelos,precios, fecha);
				for( List<Object> presupuesto: presupuestos)
				{
					String cantidadUno= String.format("%.2f", ((double)presupuesto.get(1)));
					String cantidadMulti= String.format("%.2f", ((double)presupuesto.get(1)*pasajeros));
					
					if(pasajeros==1)
					System.out.println(presupuesto.get(0) + ", "+cantidadUno+"€");
					else
					{
						System.out.println(presupuesto.get(0) + ", "+cantidadUno+"€ x "+pasajeros+" = "+cantidadMulti+"€" );
						}
						
				}
				
			}
			
			
		} catch (IOException e) {
			System.out.println("Introducidos valores no válidos");
		} catch (ParseException e) {
			System.out.println("Fecha en formato incorrecto");
		}
		catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
		
		
		
		}
		
		
	}
	
	/**
	 * Obtenemos los precios por persona para cada vuelo.
	 * 
	 * @param vuelos Lista de vuelos a buscar
	 * @param precios Lista de vuelos-precio
	 * @param fecha Fecha programada del vuelo
	 * @return Lista formada por códigos de vuelo  (String) y precio por persona (double)
	 */
	public static List<List<Object>> presupuestoVueloUnitario(List<String> vuelos,List<List<String>> precios, Date fecha)
	{
		
		List<List<Object>>presupuestos = new ArrayList<>();
		for(String vuelo :vuelos)
		{
			for(List<String>precio :precios)
			{
				if(vuelo.equals(precio.get(0)))
				{
					List<Object>presupuesto= new ArrayList<>();
					presupuesto.add(vuelo);
					presupuesto.add(calculaPrecioUnitario(fecha,precio.get(1)));
					presupuestos.add(presupuesto);
					break;
				}
			}
			
		}
		
		return presupuestos;
	}
	
	/**
	 * Calcula el precio por pasajero según la antelación.
	 * @param fechaVuelo fecha del vuelo	
	 * @param precioBase precio base del vuelo
	 * @return precio calculado
	 * @throws IllegalArgumentException excepción por fecha no válida
	 */
	private static double calculaPrecioUnitario(Date fechaVuelo,String precioBase)throws IllegalArgumentException
	{
		double precio=0;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date hoy;
		try {
			hoy = df.parse(df.format(new Date()));
			long diff = (fechaVuelo.getTime() - hoy.getTime())/ (1000 * 60 * 60 * 24);
		     
		     if(hoy.compareTo(fechaVuelo)>0)
		    	 throw new IllegalArgumentException("Error fecha anterior a la actual");
		     if(diff>30)
		     {precio = Long.parseLong(precioBase) * PRECIO_MAY_30;}
		     else if(diff>15)
		     {precio = Long.parseLong(precioBase) * PRECIO_MAY_15;}
		     else if(diff>2)
		     {precio = Long.parseLong(precioBase) * PRECIO_MAY_2;}
		     else
		     {precio = Long.parseLong(precioBase) * PRECIO_MEN_3;}
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		 
		
		
		return precio;
	}
	
	
	
	
	
	/**
	 * Obtiene todos los vuelos con el origen/destino indicado
	 * @param origen aeropuerto de origen	
	 * @param destino areopuerto destino
	 * @param rutas listado de vuelos programados
	 * @return vuelos que cumplen los requisitos
	 */
	public static List<String> obtenerRutas(String origen, String destino,List<List<String>> rutas)
	{
		List<String> retorno=new ArrayList<String>();
		for(List<String>ruta:rutas)
		{
			if(ruta.get(0).equals(origen) && ruta.get(1).equals(destino))
			{
				retorno.add(ruta.get(2));
			}
		}
		
		
		
		return retorno;
		
	}

}
