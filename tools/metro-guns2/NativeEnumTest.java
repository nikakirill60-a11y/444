import com.modularwarfare.common.guns.WeaponFireMode;
/** Uses the real closed-JAR enum, no replacement enum or Minecraft runtime required. */
public final class NativeEnumTest {
 public static void main(String[] args) {
  if(WeaponFireMode.fromString("auto")!=null)throw new AssertionError("Regression baseline changed");
  if(WeaponFireMode.fromString("full")!=WeaponFireMode.FULL)throw new AssertionError("FULL is required");
  for(String mode:args)if(WeaponFireMode.fromString(mode)==null)throw new AssertionError("Null native fire mode: "+mode);
  System.out.println("NATIVE_ENUM_TESTS_OK all output modes resolve in the real MWF JAR; legacy auto reproduced as null");
 }
}
