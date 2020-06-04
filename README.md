L2io
====
Lineage 2 client files I/O library.

Usage
-----
```java
import l2.client.io.RandomAccess;
import l2.client.io.UnrealPackage;
import java.io.File;
import java.nio.ByteBuffer;


File l2Folder = new File("C:\\Lineage 2");
File pckg = new File(new File(l2Folder, "system"), "Engine.u");
String entryName = "Actor.ScriptText";

try (UnrealPackage up = new UnrealPackage(pckg, true)) {
    UnrealPackage.ExportEntry entry = up.getExportTable()
            .stream()
            .filter(e -> e.getObjectInnerFullName().equalsIgnoreCase(entryName))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("Entry not found"));
    byte[] raw = entry.getObjectRawData();
    RandomAccess buffer = RandomAccess.randomAccess(ByteBuffer.wrap(raw), null, up.getFile().getCharset(), entry.getOffset());
    buffer.readCompactInt(); //empty properties
    buffer.readInt();        //pos
    buffer.readInt();        //top
    String text = buffer.readLine();
    System.out.println(text);
}
```

Build
-----
```
gradlew build
```
Append `-x test` to skip tests.
```
gradlew install
```

Gradle
------
dependencies {
    compile group:'l2.client', name:'L2io', version: '2.2.+'
}