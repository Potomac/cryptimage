# cryptimage

Cryptimage is an open source software under the license GNU GPL v3 which purpose is to reproduce old analog TV encryption systems.

Systems simulated are : 

- discret 11 (used between 1984 and 1995 by french TV network "canal plus") 

- nagravision syster (used between 1995 and 2010) 

- videocrypt (used by Sky TV and by several other broadcasters on astra satellites)

- MAC-Eurocrypt

This software allows to encrypt a video file (image and sound) , and allows also a decryption of an encrypted file, by meeting the standard discret11, nagravision syster, videocrypt and MAC-Eurocrypt.
Besides the ability to reproduce in a digital way these encryption systems, its second use is to allow the re-use of hardware descramblers by injecting to them an encrypted video file produced by cryptimage.

Official web site :
http://cryptimage.vot.pl

Dependencies needed to compile the source code : 

- slf4j : https://mvnrepository.com/artifact/org.slf4j/slf4j-api/1.7.7 https://mvnrepository.com/artifact/org.slf4j/slf4j-simple/1.7.7
- xuggle-xuggler 5.4 : https://mvnrepository.com/artifact/xuggle/xuggle-xuggler/5.4
