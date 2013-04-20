package ohtu.verkkokauppa;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KauppaTest {

	Kauppa kauppa;
	Pankki pankki;
	Viitegeneraattori viite;
	Varasto varasto;

	public KauppaTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
		varasto = mock(Varasto.class);
		when(varasto.saldo(1)).thenReturn(10);
		when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
		when(varasto.saldo(2)).thenReturn(50);
		when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "muna", 2));
		when(varasto.saldo(3)).thenReturn(0);
		when(varasto.haeTuote(3)).thenReturn(new Tuote(3, "jogurtti", 7));

		pankki = mock(Pankki.class);

		viite = mock(Viitegeneraattori.class);
        // määritellään että metodi palauttaa ensimmäisellä kutsukerralla 1, toisella 2,
        // kolmannella 3 ja neljännellä 4
		when(viite.uusi()).
                thenReturn(1).
                thenReturn(2).
                thenReturn(3).
                thenReturn(4);


		kauppa = new Kauppa(varasto, pankki, viite);
	}
	
	@After
	public void tearDown() {
	}
	
	@Test
	public void ostoksenPaatyttyapankinMetodiaTilisiirtoKutsutaan() {
		// luodaan ensin mock-oliot
		Pankki pankki = mock(Pankki.class);

		Viitegeneraattori viite = mock(Viitegeneraattori.class);
		when(viite.uusi()).thenReturn(1);

		Varasto varasto = mock(Varasto.class);
		when(varasto.saldo(1)).thenReturn(10);
		when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

		// sitten testattava kauppa 
		Kauppa k = new Kauppa(varasto, pankki, viite);

		// tehdään ostokset
		k.aloitaAsiointi();
		k.lisaaKoriin(1);
		k.tilimaksu("pekka", "12345");

		// sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
		verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(), anyInt());
	}

	/**
	 * Test of aloitaAsiointi method, of class Kauppa.
	 * Aloitataan asiointi, koriin lisätään koriin tuote jota varastossa on ja suoritetaan ostos (eli kutsutaan metodia kaupan tilimaksu()).
	 * varmistettava että kutsutaan pankin metodia tilisiirto oikealla asiakkaalla, tilinumerolla ja summalla
	 */
	@Test
	public void testAloitaAsiointi() {
		System.out.println("aloitaAsiointi");
		kauppa.aloitaAsiointi();
		kauppa.lisaaKoriin(1);
		kauppa.tilimaksu("erkki", "123456-123456");
//		verify(pankki).tilisiirto(String nimi, int viitenumero, String tililta, String tilille, int summa);
//		verify(pankki).tilisiirto(eq("erkki"), eq(1), eq("123456-123456"), eq("33333-44455"), eq(5));
		verify(pankki).tilisiirto(eq("erkki"), anyInt(), eq("123456-123456"), anyString(), eq(5));
	}

	/**
	 * Aloitataan asiointi, koriin lisätään koriin kaksi eri tuotetta joita varastossa on ja suoritetaan ostos.
	 * varmistettava että kutsutaan pankin metodia tilisiirto oikealla asiakkaalla, tilinumerolla ja summalla
	 */
	@Test
	public void testAsiointiKahdellaEriTuotteella() {
		System.out.println("aloitaAsiointiKahdellaEriTuotteella");
		kauppa.aloitaAsiointi();
		kauppa.lisaaKoriin(1);
		kauppa.lisaaKoriin(2);
		kauppa.tilimaksu("erkki", "123456-123456");
		verify(pankki).tilisiirto(eq("erkki"), anyInt(), eq("123456-123456"), anyString(), eq(7));
	}

	/**
	 * Aloitetaan asiointi, koriin lisätään koriin kaksi samaa tuotetta jota on varastossa tarpeeksi ja suoritetaan ostos.
	 * Varmistettava että kutsutaan pankin metodia tilisiirto oikealla asiakkaalla, tilinumerolla ja summalla
	 */
	@Test
	public void testAsiointiKahdellaSamallaTuotteella() {
		System.out.println("aloitaAsiointiKahdellaSamallaTuotteella");
		kauppa.aloitaAsiointi();
		kauppa.lisaaKoriin(1);
		kauppa.lisaaKoriin(1);
		kauppa.tilimaksu("erkki", "123456-123456");
		verify(pankki).tilisiirto(eq("erkki"), anyInt(), eq("123456-123456"), anyString(), eq(10));
	}

	/**
	 * Aloitetaan asiointi, koriin lisätään koriin tuote jota on varastossa tarpeeksi ja tuote joka on loppu ja suoritetaan ostos.
	 * Varmistettava että kutsutaan pankin metodia tilisiirto oikealla asiakkaalla, tilinumerolla ja summalla
	 */
	@Test
	public void testAsioiLoppuneellaTuotteella() {
		System.out.println("asioiLoppuneellaTuotteella");
		kauppa.aloitaAsiointi();
		kauppa.lisaaKoriin(1);
		kauppa.lisaaKoriin(3);
		kauppa.tilimaksu("erkki", "123456-123456");
		verify(pankki).tilisiirto(eq("erkki"), anyInt(), eq("123456-123456"), anyString(), eq(5));
	}

	/**
	 * Varmistettava että metodin aloita asiointi kutsuminen nollaa edellisen ostoksen tiedot
	 */
	@Test
	public void testAloitaAsiointiAlusta() {
		System.out.println("aloitaAsiointiAlusta");
		kauppa.aloitaAsiointi();
		kauppa.lisaaKoriin(1);
		kauppa.lisaaKoriin(2);
		kauppa.lisaaKoriin(3);
		kauppa.aloitaAsiointi();
		kauppa.tilimaksu("erkki", "123456-123456");
		verify(pankki).tilisiirto(eq("erkki"), anyInt(), eq("123456-123456"), anyString(), eq(0));
	}

	/**
	 * Varmistettava että kauppa pyytää uuden viitenumeron jokaiselle maksutapahtumalle
	 */
	@Test
	public void testUusiViiteMaksuTapahtumille() {
		System.out.println("uusiViiteMaksutapahtumille");
		kauppa.aloitaAsiointi();
		kauppa.lisaaKoriin(1);
		kauppa.tilimaksu("e", "123");
		verify(pankki).tilisiirto(anyString(), eq(1), anyString(), anyString(), anyInt());

		kauppa.aloitaAsiointi();
		kauppa.lisaaKoriin(1);
		kauppa.tilimaksu("e", "123");
		verify(pankki).tilisiirto(anyString(), eq(2), anyString(), anyString(), anyInt());

		kauppa.aloitaAsiointi();
		kauppa.lisaaKoriin(1);
		kauppa.tilimaksu("e", "123");
		verify(pankki).tilisiirto(anyString(), eq(3), anyString(), anyString(), anyInt());

		kauppa.aloitaAsiointi();
		kauppa.lisaaKoriin(1);
		kauppa.tilimaksu("e", "123");
		verify(pankki).tilisiirto(anyString(), eq(4), anyString(), anyString(), anyInt());
	}




	/**
	 * Test of poistaKorista method, of class Kauppa.
	 */
	@Test
	public void testPoistaKorista() {
		System.out.println("poistaKorista");
		kauppa.aloitaAsiointi();
		kauppa.lisaaKoriin(1);
		kauppa.lisaaKoriin(1);
		kauppa.poistaKorista(1);
		kauppa.tilimaksu("e", "123");
		verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(), eq(5));
//		int id = 0;
//		Kauppa instance = null;
//		instance.poistaKorista(id);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
	}

//	/**
//	 * Test of lisaaKoriin method, of class Kauppa.
//	 */
//	@Test
//	public void testLisaaKoriin() {
//		System.out.println("lisaaKoriin");
//		int id = 0;
//		Kauppa instance = null;
//		instance.lisaaKoriin(id);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of tilimaksu method, of class Kauppa.
//	 */
//	@Test
//	public void testTilimaksu() {
//		System.out.println("tilimaksu");
//		String nimi = "";
//		String tiliNumero = "";
//		Kauppa instance = null;
//		boolean expResult = false;
//		boolean result = instance.tilimaksu(nimi, tiliNumero);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
}