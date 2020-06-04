package l2.client.io;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class UnrealPackageTests {
    private static UnrealPackage createTestPackage() {
        return UnrealPackage.create(new RandomAccessMemory("test", UnrealPackage.getDefaultCharset()), 127, 32);
    }

    @Test
    public void addNameEntry() {
        try (UnrealPackage up = createTestPackage()) {
            int size = up.getNameTable().size();

            up.addNameEntries(Collections.singletonMap("name_entry", 0));

            assertEquals(size + 1, up.getNameTable().size());
        }
    }

    @Test
    public void updateNameEntry() {
        try (UnrealPackage up = createTestPackage()) {
            int size = up.getNameTable().size();

            up.addNameEntries(Collections.singletonMap("package1.entry1", 0));
            int newNameEntryIndex = up.nameReference("package1.entry1");
            up.updateNameEntry(newNameEntryIndex, "package2.entry2", 123456);
            int updatedNameEntryIndex = up.nameReference("package2.entry2");

            assertEquals(size + 1, up.getNameTable().size());
            assertEquals(newNameEntryIndex, updatedNameEntryIndex);
            assertEquals(123456, up.getNameTable().get(updatedNameEntryIndex).getFlags());
        }
    }

    @Test
    public void addImportEntry() {
        try (UnrealPackage up = createTestPackage()) {
            int size = up.getImportTable().size();

            up.addImportEntries(Collections.singletonMap("Engine.Actor", "Core.Class"));
            assertEquals(size + 2, up.getImportTable().size());

            UnrealPackage.ImportEntry newImportEntry = up.getImportTable().get(up.getImportTable().size() - 1);
            assertEquals("Engine.Actor", newImportEntry.getObjectFullName());
            assertEquals("Core.Class", newImportEntry.getFullClassName());

            up.addImportEntries(Collections.singletonMap("Engine.Actor", "Core.Class"));
            assertEquals(size + 2, up.getImportTable().size());

            up.addImportEntries(Collections.singletonMap("Engine.Emitter", "Core.Class"));
            assertEquals(size + 3, up.getImportTable().size());
        }
    }

    @Test
    public void removeImport() {
        try (UnrealPackage up = createTestPackage()) {
            up.addImportEntries(Collections.singletonMap("Engine.Actor", "Core.Class"));
            UnrealPackage.ImportEntry newImportEntry = up.getImportTable().get(up.getImportTable().size() - 1);
            assertEquals("Engine.Actor", newImportEntry.getObjectFullName());
            assertEquals("Core.Class", newImportEntry.getFullClassName());

            int newEntryIndex = newImportEntry.getIndex();
            up.renameImport(newEntryIndex, "Engine.Emitter");
            assertEquals("Engine.Emitter", up.getImportTable().get(newEntryIndex).getObjectFullName());
        }
    }

    @Test
    public void changeImportClass() {
        try (UnrealPackage up = createTestPackage()) {
            up.addImportEntries(Collections.singletonMap("Engine.Actor", "Core.Class"));
            UnrealPackage.ImportEntry newImportEntry = up.getImportTable().get(up.getImportTable().size() - 1);
            assertEquals("Engine.Actor", newImportEntry.getObjectFullName());
            assertEquals("Core.Class", newImportEntry.getFullClassName());

            int newEntryIndex = newImportEntry.getIndex();
            up.changeImportClass(newEntryIndex, "Core.Clazz");
            assertEquals("Core.Clazz", up.getImportTable().get(newEntryIndex).getFullClassName());
        }
    }

    @Test
    public void addExportEntry() {
        try (UnrealPackage up = createTestPackage()) {
            up.addExportEntry("test.texture", "Engine.Texture", null, new byte[5], 0);
            up.addExportEntry("test.texture.sub", "Engine.Texture", null, new byte[5], 0);
            assertEquals(3, up.getExportTable().size());
        }
    }

    @Test
    public void removeExportEntry() {
        try (UnrealPackage up = createTestPackage()) {
            up.addExportEntry("test.texture", "Engine.Texture", null, new byte[5], 0);
            int newExportEntryIndex = up.getExportTable().size() - 1;
            up.removeExport(newExportEntryIndex);
            UnrealPackage.ExportEntry entry = up.getExportTable().get(newExportEntryIndex);
            assertEquals("Core.Package", entry.getFullClassName());
        }
    }
}
