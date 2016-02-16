;Start
!define MUI_PRODUCT "Shadi Cards Inventory"
!define MUI_FILE "cardinventory"
CRCCheck On

;General
OutFile "inventory_installx32.exe"

;Folder selection page
InstallDir "$PROGRAMFILES\${MUI_PRODUCT}"

;Installer Section
Section "install"

	SetOutPath "$INSTDIR"
	
	SetOverwrite On

	File ${MUI_FILE}.exe
	File LICENSE.txt
	File logo.png
	File /r jre1.8.0_71
	File /r dist

	;Create desktop shortcuts
	CreateShortCut "$DESKTOP\${MUI_PRODUCT}.lnk" "$INSTDIR\${MUI_FILE}.exe" ""

	;Create start-menu items
	CreateDirectory "$SMPROGRAMS\${MUI_PRODUCT}"
	CreateShortCut "$SMPROGRAMS\${MUI_PRODUCT}\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0
	CreateShortCut "$SMPROGRAMS\${MUI_PRODUCT}\${MUI_PRODUCT}.lnk" "$INSTDIR\${MUI_FILE}.exe" "" "$INSTDIR\${MUI_FILE}.exe" 0

	;write uninstall information to the registry
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "DisplayName" "${MUI_PRODUCT} (remove only)"
	WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}" "UninstallString" "$INSTDIR\Uninstall.exe"

	WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

;Uninstaller Section
Section "Uninstall"

	;Delete files
	RMDir /r "$INSTDIR\dist\*.*"
	RMDir /r "$INSTDIR\*.*"

	;Remove the installation directory
	RMDir "$INSTDIR"

	;Delete Start Menu Shortcuts
	Delete "$DESKTOP\${MUI_PRODUCT}.lnk"
	Delete "$SMPROGRAMS\${MUI_PRODUCT}\*.*"
	RMDir "$SMPROGRAMS\${MUI_PRODUCT}"

	;Delete Uninstaller and Uninstall Registry Entries
	DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\${MUI_PRODUCT}"
	DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${MUI_PRODUCT}"

SectionEnd

;eof