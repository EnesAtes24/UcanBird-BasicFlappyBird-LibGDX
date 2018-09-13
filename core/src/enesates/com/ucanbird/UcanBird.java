package enesates.com.ucanbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import java.util.Random;

public class UcanBird extends ApplicationAdapter {

	SpriteBatch batch; // batch denen şey sprite(obje,nesne) leri çizmemize yardımcı olur.
	Texture background; //Texture bir nesnenin imajını aldığı patern(Kuşun resmi, arkaplanın resmi gibi.)
	Texture bird;
	Texture bee1;
	Texture bee2;
	Texture bee3;
	float birdX = 0;
	float birdY = 0;
	int gameState = 0; // Oyun başlamamışkenki durumu 0 atadık. Oyun başlayınca biz bunu 1 e çeviricez.
	float velocity = 0; // Hız kastediliyr yerçekimi ile kullanıcaz.
	float gravity = 0.8f;
	float enemyVelocity = 10; // enemynin hızını
	Random random;
	int score = 0;
	int scoredEnemy = 0; // Eğer kuş arıları geçtiyse score'u 1 arttır.
	BitmapFont font; // Oyun bittiğinde score'u yazdırmak için kulanıcaz.
	BitmapFont font2;

	Circle birdCircle;

	ShapeRenderer shapeRenderer; // Bunu oyunun içinde kullanmıycaz sadece circle yaparken doğru yerdemi  diye renk  vermek için kullanıcaz.

	int numberOfEnemies = 4;
	float [] enemyX = new float[numberOfEnemies];
	float [] enemyOffSet1 = new float[numberOfEnemies]; // Bunlar y ekseninde random olarak gelmesi için yazdık.
	float [] enemyOffSet2 = new float[numberOfEnemies];
	float [] enemyOffSet3 = new float[numberOfEnemies];
	float distance = 0; // Bu enemyler arasına belirli bir mesafe koymak için kullanılacak.

	Circle [] enemyCircle1;
	Circle [] enemyCircle2;
	Circle [] enemyCircle3;


	@Override
	public void create () {
		// onCreate ile aynı şey aslında yani bir uygulma açıldığında olacak şeyleri onun altına yazıyoruz.
		// Bir obje oluşturulduğunda başlatma yani initialize etme bunun altında yapılır.

		batch = new SpriteBatch();
		background = new Texture("background.png");
		bird = new Texture("bird.png");
		bee1 = new Texture("bee.png");
		bee2 = new Texture("bee.png");
		bee3 = new Texture("bee.png");

		distance = Gdx.graphics.getWidth() / 2; // Tablet, telefon gibi değişik ekranlara uyumlu olsun diye bu şekilde tanımlama yapıyoruz.
		random = new Random();


		// X ekseni sabit kalsada Y eksenini sürekli değişeceği için birdX, birdY diye iki değişken tanımlamamız iyi oldu.
		birdX = Gdx.graphics.getWidth() / 4;
		birdY = Gdx.graphics.getHeight() / 2;

		//shapeRenderer = new ShapeRenderer();

		birdCircle = new Circle();
		enemyCircle1 = new Circle[numberOfEnemies];
		enemyCircle2 = new Circle[numberOfEnemies];
		enemyCircle3 = new Circle[numberOfEnemies];

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(4);

		font2 = new BitmapFont();
		font2.setColor(Color.WHITE);
		font2.getData().setScale(6);

		for(int i = 0; i<numberOfEnemies; i++) {

			// Random bir numarayla y ekseni oluştarmaya çalışıcaz. random.nextFloat() bize 0 ile 1 arasında bir yüzde vericek.
			enemyOffSet1[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
			enemyOffSet2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
			enemyOffSet3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);

			enemyX[i] = Gdx.graphics.getWidth() - bee1.getWidth() / 2 + i * distance;

			enemyCircle1[i] = new Circle();
			enemyCircle2[i] = new Circle();
			enemyCircle3[i] = new Circle();

		}


	}

	@Override
	public void render () {
		// Oyun devam ettiği sürece çağrılan bir metod, oyun devam ettiği sürece olacak şeyleri burada yazıyoruz o yüzden burası en çok işlem yapacağımız yer.
		// Mesela oyun devam ederken sürekli olmasını istediğimiz şeyleri kuşun uçması, düşmanların gelmesi vs burada yazıcaz.

		batch.begin(); // batch.begin() ile batch.end() arasında ne çizeceğimizi hangi objelerin olacağını yazıcaz.
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // draw() fonksiyonu ne çizeceğimizi, hangi texture'ı kullanacağımızı, hangi boyutlarda çizeceğimizi, ekranda nereye yerleştireceğimizi vs. sorar.


		if(gameState == 1) { // Eğer oyun başladıysa ne olacağını yaz.

			if(enemyX[scoredEnemy] <  Gdx.graphics.getWidth() / 4) { //enemy bird'ten daha geride bir yerdeyse
				score ++;

				if(scoredEnemy < numberOfEnemies -1) {
					scoredEnemy ++;
				} else {
					scoredEnemy = 0;
				}
			}

			if(Gdx.input.justTouched()) { //Gdx kütüphanesinin imkanlarıyla ekrana dokunma özeliği verdik(input ile bir çok işlem yapabiliyoruz.)
				velocity = -15; // yani ekrana dokunulduğunda kuşu yukarı zıplat diyoruz.
			}

			for(int i = 0; i < numberOfEnemies; i++){

				if(enemyX[i] < Gdx.graphics.getWidth() / 15) { // Burada da arıların(enemy) sonsuz döngüye girmesi için eğer genişliğinin altına düşmüşse başa al diyoruz.
					enemyX[i] = enemyX[i] + numberOfEnemies * distance;

					// Burada -0.5f dememizin sebebi eğer 0.9 ise artı bir değer 0.1  ise eksi bir değer vericek ve y ekseninin hem altında hem üstünde değerler vermiş olacak her seferinde.
					enemyOffSet1[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
					enemyOffSet2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
					enemyOffSet3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);

				} else { // Eğer böyle bir problem yoksa devam et diyoruz.
					enemyX[i] = enemyX[i] - enemyVelocity;
				}

				// Oyun başlamadan arıları göstermenin bir mantığı yok o sebepten burada tanımladık.
				// Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10 yaparak kuşumuzun boyutuyla enemy'nin boyutunu eşit tutuyoruz.
				batch.draw(bee1, enemyX[i] , Gdx.graphics.getHeight() / 2 + enemyOffSet1[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
				batch.draw(bee2, enemyX[i] , Gdx.graphics.getHeight() / 2 + enemyOffSet2[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
				batch.draw(bee3, enemyX[i] , Gdx.graphics.getHeight() / 2 + enemyOffSet3[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);

				// enemy için x, y ve radius değerleri enemylerin bulunduğu yerlerin merkezine almak için birkaç satır yukarıda vrilen değerlere göre orantılandı. incele yüksekliğin yarısını eklemek gibi.
				enemyCircle1[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffSet1[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 30);
				enemyCircle2[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffSet2[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 30);
				enemyCircle3[i] = new Circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffSet3[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 30);
				// Bu kodların çalışıp çalışmadığını anlamak için shapeRender metoduna başvurduk aşağıda.
			}





			if(birdY > 0 && birdY < Gdx.graphics.getHeight()) { // Ekrandan aşağı doğru sonsuza kadar gitmesin ve yukarı çok gitmesin diye kontrol yapıyoruz.

				velocity += gravity; // Her render() çalıştığında ve velocityi gravity ile arttırıcaz.
				birdY = birdY - velocity ; // Yerçekimi oluşturmaya çalışıyoruz.
			} else {
				gameState = 2; // Kuş aşağı düşerse oyunu bitir.
			}

		} else if(gameState == 0) { // Burada eğer oyun başlamadıysa başlangıç durumunda bekle diyoruz.

			if(Gdx.input.justTouched()) { //Gdx kütüphanesinin imkanlarıyla ekrana dokunma özeliği verdik(input ile bir çok işlem yapabiliyoruz.)
				gameState = 1; // Oyun başladı demek istiyoruz mesela oyun bittiğinde de 2 verebiliriz. Yani oyun başladı mı bitti mi diye değişkenler oluşturuyoruz rakamların yerine başka değişkenler vererek de yapabiliriz, String vs.
			}

		} else if(gameState == 2){

			font2.draw(batch,"Game Over! Tap to play again...", Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2);

			if(Gdx.input.justTouched()) { //Gdx kütüphanesinin imkanlarıyla ekrana dokunma özeliği verdik(input ile bir çok işlem yapabiliyoruz.)
				gameState = 1; // Oyun başladı demek istiyoruz mesela oyun bittiğinde de 2 verebiliriz. Yani oyun başladı mı bitti mi diye değişkenler oluşturuyoruz rakamların yerine başka değişkenler vererek de yapabiliriz, String vs.
				birdY = Gdx.graphics.getHeight() / 2;

				for(int i = 0; i<numberOfEnemies; i++) {

					// Random bir numarayla y ekseni oluştarmaya çalışıcaz. random.nextFloat() bize 0 ile 1 arasında bir yüzde vericek.
					enemyOffSet1[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
					enemyOffSet2[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
					enemyOffSet3[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);

					enemyX[i] = Gdx.graphics.getWidth() - bee1.getWidth() / 2 + i * distance;

					enemyCircle1[i] = new Circle();
					enemyCircle2[i] = new Circle();
					enemyCircle3[i] = new Circle();

				}

				// Oyun bittiyse sıfırladık.
				velocity = 0; // Eğer çok yukarı çıktıysa baştan başasın diye.
				scoredEnemy = 0;
				score = 0;

			}
		}


		batch.draw(bird, birdX, birdY, Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
		// Gdx.graphics.getWidth() / 3 gibi oranlar yazmamızın sebebi farklı ortamla uyumlu olsun diye.
		font.draw(batch,String.valueOf(score), 100, 200);
		batch.end();

		//Burada x ve y eksenlerine kuşumuzun yüksekliği ve genişliğinin yarısı kadar ekleme yaptık çünkü circle'ı merkezde başlata bilmek için.
		birdCircle.set(birdX + Gdx.graphics.getWidth() / 30, birdY + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 30); // yarı çapımız Gdx.graphics.getWidth() / 15 in yarısı kadar olmalı mantıken.

		// shapeRenderer ile renk vererek doğru bir şekilde daire içine aldıkmı onu görüyoruz.
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // Filled  içi dolu olsun dedik
		//shapeRenderer.setColor(Color.BLUE);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius); // birdCircle ile eşitlrmiş olduk.


		for(int i = 0; i < numberOfEnemies; i++){
			//shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffSet1[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 30);
			//shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffSet2[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 30);
			//shapeRenderer.circle(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffSet3[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 30);

			// Çarpışmları kontrol edeceğiz. Bunu Intersector.overlaps() ile yapıyoruz.
			if(Intersector.overlaps(birdCircle, enemyCircle1[i]) || Intersector.overlaps(birdCircle, enemyCircle2[i]) || Intersector.overlaps(birdCircle, enemyCircle3[i])){
				gameState = 2; // Oyun bitir demek istiyoruz yukarıda ne bitirmesi kod yazıldı.
			}

		}
		//shapeRenderer.end();

	}
	
	@Override
	public void dispose () {

	}
}
