#comments-start
	
	Title: PhotoRec Sorter
	Version: 1.0.0.9
	Author: Sam Leavens (builtBackwards)
	Author URL: http://builtbackwards.com
	Author Email: contact@builtbackwards.com
	Project URL: http://builtbackwards.com/projects/photorec-sorter/
	
	Description:
	This is a simple script to sort files recoverd by PhotoRec.
	If you have any comments, but reports, or suggestions feel free
	to send an email to the Author Email address above.
	
	I would like to thank an unnamed scripter from the AutoIt forums
	for the FileSearch function.
	
	License:
	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
	
#comments-end

Opt("TrayIconHide", 1)

$arr = _FileSearch(@ScriptDir & "\recup_dir*", 0, 0, 0, 1)

If $arr[0] == 0 Then
	ConsoleWrite("Cant find any recup_dir folders!" & @CRLF & "Exiting....");
	Sleep(5000)
	Exit
EndIf

For $my_i = 1 To $arr[0] Step 1

	$search = FileFindFirstFile($arr[$my_i] & "\*.*")

	; Check if the search was successful
	If $search = -1 Then
		ConsoleWrite("Cant find a file!" & @CRLF);
		_DelEmptyDir($arr[$my_i])
		ConsoleWrite("Exiting....");
		Sleep(5000)
		Exit
	EndIf

	While 1 ; Search was successful

		$file = FileFindNextFile($search)
		If @error Then ExitLoop

		$ext = StringSplit($file, ".")


		$path = $arr[$my_i] & "\" & $file
		$dir = @ScriptDir & "\" & StringUpper($ext[$ext[0]])

		If FileExists($dir) Then
			ConsoleWrite("Folder " & $dir & " already exists" & @CRLF)
		Else
			DirCreate($dir)
			ConsoleWrite("Created folder " & $dir & @CRLF)
		EndIf

		If FileMove($path, $dir) Then ConsoleWrite("Moved " & $path & " to " & $dir & @CRLF)

	WEnd

	; Close the search handle
	FileClose($search)

	;Check if current recup_dir folder is empty and delete
	_DelEmptyDir($arr[$my_i])


Next

ConsoleWrite(@CRLF & "All Done!" & @CRLF);
Sleep(5000)

Func _DelEmptyDir($current_dir) ; Checks if current recup_dir folder is empty then deletes it
	$size = DirGetSize($current_dir)

	If $size == 0 Then
		If DirRemove($current_dir) Then ConsoleWrite("Removed empty folder " & $current_dir & @CRLF)
	EndIf
EndFunc   ;==>_DelEmptyDir


; _FileSearch( "Path and Mask", <$nOption>, <$cpusaver>, <$dirfilter>, <$filefilter>)
;
;----PARAMETERS-----
;$nOption -   <Optional (0 - normal, 1- recursive)>
;$cpusaver -  <Optional (0 - normal, 1 - CPU Friendly, but Slower)>
;$dirfilter - <Optional (0 - list directories, 1 - filter out directories)>
;$filefilter- <Optional (0 - list files, 1 - filter out files)>
;
;----RETURN-----
; Returns array. Either Array of files (full path) where...
; Array[0] is number of files.
; Array[0] = 0 if nothing found.
; EXAMPLE USAGE
;--------------------------------------------
;~ #include<array.au3>
;~ $a = _FileSearch("C:\*.*", 1, 0, 0, 0)
;~ _ArrayDisplay($a)
;--------------------------------------------
Func _FileSearch($szMask, $nOption = 0, $cpusaver = 0, $dirfilter = 0, $filefilter = 0)
	$szRoot = ""
	$hFile = 0
	$szBuffer = ""
	$szReturn = ""
	$szPathList = "*"
	Dim $aNULL[1]

	If Not StringInStr($szMask, "\") Then
		$szRoot = @ScriptDir & "\"
	Else
		While StringInStr($szMask, "\")
			$szRoot = $szRoot & StringLeft($szMask, StringInStr($szMask, "\"))
			$szMask = StringTrimLeft($szMask, StringInStr($szMask, "\"))
		WEnd
	EndIf
	If $nOption = 0 Then
		_FileSearchUtil($szRoot, $szMask, $szReturn, $cpusaver, $dirfilter, $filefilter)
	Else
		While 1
			$hFile = FileFindFirstFile($szRoot & "*.*")
			If $hFile >= 0 Then
				$szBuffer = FileFindNextFile($hFile)
				While Not @error
					If $szBuffer <> "." And $szBuffer <> ".." And _
							StringInStr(FileGetAttrib($szRoot & $szBuffer), "D") Then _
							$szPathList = $szPathList & $szRoot & $szBuffer & "*"
					$szBuffer = FileFindNextFile($hFile)
				WEnd
				FileClose($hFile)
			EndIf
			_FileSearchUtil($szRoot, $szMask, $szReturn, $cpusaver, $dirfilter, $filefilter)
			If $szPathList == "*" Then ExitLoop
			$szPathList = StringTrimLeft($szPathList, 1)
			$szRoot = StringLeft($szPathList, StringInStr($szPathList, "*") - 1) & "\"
			$szPathList = StringTrimLeft($szPathList, StringInStr($szPathList, "*") - 1)
		WEnd
	EndIf
	If $szReturn = "" Then
		$aNULL[0] = 0
		Return $aNULL
	Else
		Return StringSplit(StringTrimRight($szReturn, 1), "*")
	EndIf
EndFunc   ;==>_FileSearch

Func _FileSearchUtil(ByRef $ROOT, ByRef $MASK, ByRef $RETURN, $cpusaver, $dirfilter, $filefilter)
	$hFile = FileFindFirstFile($ROOT & $MASK)
	If $hFile >= 0 Then
		$szBuffer = FileFindNextFile($hFile)
		While Not @error
			If $cpusaver = 1 Then Sleep(1) ;OPTIONAL FOR CPU SAKE
			If $szBuffer <> "." And $szBuffer <> ".." Then
				If StringInStr(FileGetAttrib($ROOT & $szBuffer), "D") Then
					If $dirfilter = 0 Then $RETURN = $RETURN & $ROOT & $szBuffer & "*"
				Else
					If $filefilter = 0 Then $RETURN = $RETURN & $ROOT & $szBuffer & "*"
				EndIf
			EndIf
			$szBuffer = FileFindNextFile($hFile)
		WEnd
		FileClose($hFile)
	EndIf
EndFunc   ;==>_FileSearchUtil