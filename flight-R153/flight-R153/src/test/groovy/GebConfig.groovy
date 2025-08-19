import org.openqa.selenium.firefox.FirefoxDriver
import infrastructure.config.profiles.ahmed
import org.openqa.selenium.firefox.FirefoxBinary
import org.openqa.selenium.firefox.FirefoxProfile

def _myProfile = ahmed
def _myEnv =  _myProfile.local
//add universal env properties
_myEnv.putAll(_myProfile.universal)

driver  =
	{
		
		//clean up discard directory
		File _binary = new File(_myProfile.firefoxBinaryPath)
		FirefoxBinary ffBinary = new FirefoxBinary(_binary)
		FirefoxProfile fxProfile = new FirefoxProfile();
		fxProfile.setPreference("browser.download.folderList",2);
		fxProfile.setPreference("browser.helperApps.alwaysAsk.force", false);
		fxProfile.setPreference("browser.download.manager.showWhenStarting",false);
		fxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/zip, application/xml");
		newDriver = new FirefoxDriver(ffBinary, fxProfile)
		newDriver.manage().window().setPosition(_myEnv.browserPosition)
		newDriver.manage().window().setSize(_myEnv.browserSize)
		newDriver 
		
	}
	
reportsDir = "target/geb-reports"
baseUrl = _myEnv.baseUrl
env = _myEnv
atCheckWaiting=true













