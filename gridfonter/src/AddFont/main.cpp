#include <windows.h>

int main(int argc, char **argv) {
	if (argc>1) {
		AddFontResource(argv[1]);
		SendMessage(HWND_BROADCAST, WM_FONTCHANGE, 0, 0);
		return 0;
	}
	return 1;
}
