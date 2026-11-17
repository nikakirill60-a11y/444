using System.IO.Compression;

namespace MinETRO.Core;
public static class SafeArchive
{
    public static void Extract(string zip, string destination, string prefix, bool engine, CancellationToken ct)
    {
        var root = prefix.TrimEnd('/'); if (root.Length != 0) { Check.Relative(root); root += "/"; }
        Directory.CreateDirectory(destination); long total = 0; int files = 0;
        var seen = new HashSet<string>(StringComparer.OrdinalIgnoreCase);
        using var archive = ZipFile.OpenRead(zip);
        if (archive.Entries.Count > 100000) throw new InvalidDataException("Слишком много файлов в архиве.");
        foreach (var entry in archive.Entries)
        {
            ct.ThrowIfCancellationRequested();
            var name = entry.FullName;
            // Validate even ignored files, including zip-slip and Unix symbolic links.
            Check.Relative(name.TrimEnd('/'));
            if (((entry.ExternalAttributes >> 16) & 0xF000) == 0xA000) throw new InvalidDataException("Ссылки в ZIP запрещены.");
            if (root.Length != 0 && !name.StartsWith(root, StringComparison.Ordinal)) continue;
            var relative = name[root.Length..]; if (relative.Length == 0) continue;
            if (relative.EndsWith('/')) { Directory.CreateDirectory(Check.Under(destination, relative.TrimEnd('/'))); continue; }
            if (!seen.Add(relative)) throw new InvalidDataException("Архив содержит повторяющиеся имена файлов.");
            if (!engine)
            {
                var ext = Path.GetExtension(relative).ToLowerInvariant();
                if (new[] { ".exe", ".dll", ".bat", ".cmd", ".ps1", ".vbs", ".js", ".lnk", ".url", ".scr", ".com", ".msi" }.Contains(ext))
                    throw new InvalidDataException("Архив сборки содержит исполняемые файлы Windows. Нужен архив игровых данных, не установщик.");
                if (relative.Split('/').Any(p => p.Equals("accounts.json", StringComparison.OrdinalIgnoreCase) || p.Equals("launcher_accounts.json", StringComparison.OrdinalIgnoreCase) || p.Equals(".git", StringComparison.OrdinalIgnoreCase)))
                    throw new InvalidDataException("Удалите из архива учётные данные и служебные папки.");
            }
            total = checked(total + entry.Length);
            if (entry.Length > 2L * 1024 * 1024 * 1024 || total > 16L * 1024 * 1024 * 1024) throw new InvalidDataException("Архив превышает лимит распаковки.");
            var path = Check.Under(destination, relative); Directory.CreateDirectory(Path.GetDirectoryName(path)!);
            using var input = entry.Open(); using var output = new FileStream(path, FileMode.CreateNew);
            var buffer = new byte[131072]; long copied = 0; int count;
            while ((count = input.Read(buffer)) != 0)
            {
                ct.ThrowIfCancellationRequested(); copied += count;
                if (copied > entry.Length) throw new InvalidDataException("Повреждённый ZIP.");
                output.Write(buffer, 0, count);
            }
            if (copied != entry.Length) throw new InvalidDataException("Неполный файл в ZIP."); files++;
        }
        if (files == 0) throw new InvalidDataException("В указанной папке архива нет файлов. Проверьте archiveRoot.");
    }
}
