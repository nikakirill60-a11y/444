"""Windows UI: F1 toggles a menu; spawning requires a verified game backend."""
import ctypes
from ctypes import wintypes
import os
import queue
import threading
import tkinter as tk
from tkinter import messagebox, ttk

from .catalog import CATEGORIES, search_items
from . import game_backend

HOTKEY_ID = 0x4D45
VK_F1 = 0x70
WM_HOTKEY = 0x0312
WM_QUIT = 0x0012


class F1Hotkey:
    def __init__(self, events):
        self.events = events
        self.thread_id = None
        self.ready = threading.Event()
        self.thread = threading.Thread(target=self._run, daemon=True, name="F1 hotkey")

    def start(self):
        self.thread.start()
        self.ready.wait(timeout=5)

    def _run(self):
        user32 = ctypes.windll.user32
        kernel32 = ctypes.windll.kernel32
        user32.RegisterHotKey.argtypes = (wintypes.HWND, ctypes.c_int,
                                           wintypes.UINT, wintypes.UINT)
        user32.RegisterHotKey.restype = wintypes.BOOL
        user32.GetMessageW.argtypes = (ctypes.POINTER(wintypes.MSG),
                                        wintypes.HWND, wintypes.UINT, wintypes.UINT)
        user32.GetMessageW.restype = ctypes.c_int
        self.thread_id = kernel32.GetCurrentThreadId()
        if not user32.RegisterHotKey(None, HOTKEY_ID, 0, VK_F1):
            self.events.put(("error", "Не удалось назначить F1: клавиша занята другой программой."))
            self.ready.set()
            return
        self.ready.set()
        try:
            msg = wintypes.MSG()
            while user32.GetMessageW(ctypes.byref(msg), None, 0, 0) > 0:
                if msg.message == WM_HOTKEY and msg.wParam == HOTKEY_ID:
                    self.events.put(("toggle", None))
        finally:
            user32.UnregisterHotKey(None, HOTKEY_ID)

    def stop(self):
        if self.thread_id and self.thread.is_alive():
            ctypes.windll.user32.PostThreadMessageW(self.thread_id, WM_QUIT, 0, 0)
            self.thread.join(timeout=2)


class Menu:
    def __init__(self):
        self.events = queue.Queue()
        self.hotkey = F1Hotkey(self.events)
        self.root = tk.Tk()
        self.root.title("Metro Last Light — меню предметов (F1)")
        self.root.geometry("620x540")
        self.root.minsize(530, 420)
        self.root.configure(bg="#141a1b")
        self.root.protocol("WM_DELETE_WINDOW", self.hide)
        self.root.bind("<Escape>", lambda _: self.hide())

        style = ttk.Style(self.root)
        style.theme_use("clam")
        style.configure("TFrame", background="#141a1b")
        style.configure("TLabel", background="#141a1b", foreground="#e2e8df", font=("Segoe UI", 10))
        style.configure("TButton", padding=7, font=("Segoe UI", 10))

        frame = ttk.Frame(self.root, padding=16)
        frame.pack(fill="both", expand=True)
        tk.Label(frame, text="МЕТРО  ·  ПРЕДМЕТЫ", bg="#141a1b", fg="#dfbb78",
                 font=("Segoe UI", 18, "bold")).pack(anchor="w")
        tk.Label(frame, text="F1 — открыть / скрыть     ·     Esc — скрыть",
                 bg="#141a1b", fg="#8b9996").pack(anchor="w", pady=(0, 12))
        tk.Label(frame, text="⚠ Выдача пока не подключена к игре. Нажатие кнопки ничего не изменит.",
                 bg="#382a1d", fg="#ffdb9b", wraplength=570, justify="left",
                 padx=10, pady=9).pack(fill="x", pady=(0, 12))

        controls = ttk.Frame(frame)
        controls.pack(fill="x")
        self.category = tk.StringVar(value="Все")
        category = ttk.Combobox(controls, textvariable=self.category,
                                 values=CATEGORIES, state="readonly", width=16)
        category.pack(side="left", padx=(0, 8))
        category.bind("<<ComboboxSelected>>", lambda _: self.refresh())
        self.query = tk.StringVar()
        self.query.trace_add("write", lambda *_: self.refresh())
        ttk.Entry(controls, textvariable=self.query).pack(side="left", fill="x", expand=True)
        ttk.Label(controls, text="  Поиск").pack(side="left")

        self.listbox = tk.Listbox(frame, bg="#20292a", fg="#edf2e6", selectbackground="#9e693d",
                                  selectforeground="white", relief="flat", font=("Segoe UI", 11),
                                  activestyle="none", exportselection=False)
        self.listbox.pack(fill="both", expand=True, pady=12)
        self.items = ()
        self.refresh()

        bottom = ttk.Frame(frame)
        bottom.pack(fill="x")
        ttk.Label(bottom, text="Количество:").pack(side="left")
        self.amount = tk.StringVar(value="1")
        tk.Spinbox(bottom, from_=1, to=999, textvariable=self.amount, width=6,
                   font=("Segoe UI", 10)).pack(side="left", padx=8)
        ttk.Button(bottom, text="Выдать (нужен адаптер)", command=self.spawn).pack(side="left", padx=8)
        ttk.Button(bottom, text="Выход", command=self.quit).pack(side="right")
        self.status = tk.StringVar(value="Меню готово. Игра не подключена.")
        ttk.Label(frame, textvariable=self.status, wraplength=560).pack(anchor="w", pady=(12, 0))
        self.hotkey.start()
        self.root.after(80, self.process_events)

    def refresh(self):
        old_key = None
        if self.listbox.curselection():
            old_key = self.items[self.listbox.curselection()[0]].key
        self.items = search_items(self.category.get(), self.query.get())
        self.listbox.delete(0, "end")
        for item in self.items:
            self.listbox.insert("end", f"{item.name}    ·    {item.category}")
        for index, item in enumerate(self.items):
            if item.key == old_key:
                self.listbox.selection_set(index)
                break

    def spawn(self):
        selected = self.listbox.curselection()
        if not selected:
            self.status.set("Сначала выбери предмет.")
            return
        try:
            quantity = int(self.amount.get())
            if not 1 <= quantity <= 999:
                raise ValueError
        except ValueError:
            self.status.set("Количество должно быть целым числом от 1 до 999.")
            return
        item = self.items[selected[0]]
        try:
            game_backend.spawn(item.key, quantity)
        except game_backend.SpawnUnavailable as exc:
            self.status.set(str(exc))
            messagebox.showwarning("Предмет не выдан", str(exc), parent=self.root)
        except Exception as exc:
            self.status.set(f"Ошибка выдачи: {exc}")
            messagebox.showerror("Ошибка выдачи", str(exc), parent=self.root)
        else:
            self.status.set(f"Запрос на выдачу: {item.name} × {quantity}")

    def process_events(self):
        while True:
            try:
                action, message = self.events.get_nowait()
            except queue.Empty:
                break
            if action == "toggle":
                if self.root.state() == "withdrawn":
                    self.show()
                else:
                    self.hide()
            elif action == "error":
                self.status.set(message)
        self.root.after(80, self.process_events)

    def show(self):
        self.root.deiconify()
        self.root.attributes("-topmost", True)
        self.root.lift()
        self.root.focus_force()

    def hide(self):
        self.root.withdraw()

    def quit(self):
        self.hotkey.stop()
        self.root.destroy()


def main():
    if os.name != "nt":
        raise SystemExit("Меню с глобальной клавишей F1 работает только в Windows.")
    menu = Menu()
    menu.root.mainloop()


if __name__ == "__main__":
    main()
